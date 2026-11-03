package shelter.view;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.DefaultListModel;
import javax.swing.JDialog;
import javax.swing.JList;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableModel;

import shelter.database.JsonDatabase;
import shelter.model.AdoptionRequest;
import shelter.model.Animal;
import shelter.model.Adopter;
import shelter.model.Donation;
import shelter.model.Donor;
import shelter.model.Event;
import shelter.model.ShelterModel;
import shelter.model.User;
import shelter.model.Validator;
import shelter.model.Volunteer;
import shelter.service.AuthenticationService;
import shelter.patterns.factory.strategy.AbstractNotificationFactory;
import shelter.patterns.factory.strategy.AbstractPaymentFactory;
import shelter.patterns.factory.user.AbstractUserFactory;
import shelter.patterns.observer.Observer;
import shelter.patterns.strategy.PaymentStrategy;

public class AdminDashboardPanel extends JPanel {
    private static final String[] HOME_TYPES = {"Apartment", "House with garden", "Farm"};
    private final ShelterModel model;
    private final JsonDatabase database;
    private final List<AbstractUserFactory> userFactories;
    private final List<AbstractNotificationFactory> notificationFactories;
    private final List<AbstractPaymentFactory> paymentFactories;
    private final User admin;
    private final Runnable logoutAction;
    private final DefaultTableModel tableModel = new DefaultTableModel();
    private final JTable table = new JTable(tableModel);
    private final JLabel sectionLabel = new JLabel("Users");
    private final JPanel userFilters = new JPanel();
    private final JTextField userSearchField = new JTextField(16);
    private final JComboBox<String> userRoleFilter;
    private String section = "Users";

    public AdminDashboardPanel(ShelterModel model, JsonDatabase database, User admin,
            List<AbstractUserFactory> userFactories, List<AbstractNotificationFactory> notificationFactories,
            List<AbstractPaymentFactory> paymentFactories, Runnable logoutAction) {
        this.model = model;
        this.database = database;
        this.userFactories = userFactories;
        this.notificationFactories = notificationFactories;
        this.paymentFactories = paymentFactories;
        this.admin = admin;
        this.logoutAction = logoutAction;
        this.userRoleFilter = new JComboBox<>(roleOptions(userFactories));
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(18, 18, 18, 18));
        add(createHeader(), BorderLayout.NORTH);
        add(new JScrollPane(table), BorderLayout.CENTER);
        refresh();
    }

    private static String[] roleOptions(List<AbstractUserFactory> userFactories) {
        String[] options = new String[userFactories.size() + 1];
        options[0] = "All roles";
        for (int i = 0; i < userFactories.size(); i++) {
            options[i + 1] = userFactories.get(i).getUserType();
        }
        return options;
    }

    private JPanel createHeader() {
        JPanel header = new JPanel(new BorderLayout());
        sectionLabel.setText("Admin: " + admin.getName() + " | Users");
        header.add(sectionLabel, BorderLayout.WEST);
        userFilters.add(new JLabel("Search:"));
        userFilters.add(userSearchField);
        userFilters.add(new JLabel("Role:"));
        userFilters.add(userRoleFilter);
        header.add(userFilters, BorderLayout.CENTER);
        userSearchField.getDocument().addDocumentListener(new DocumentListener() {
            @Override public void insertUpdate(DocumentEvent event) { refresh(); }
            @Override public void removeUpdate(DocumentEvent event) { refresh(); }
            @Override public void changedUpdate(DocumentEvent event) { refresh(); }
        });
        userRoleFilter.addActionListener(event -> refresh());
        JPanel buttons = new JPanel(new GridLayout(0, 4, 5, 5));
        addSectionButton(buttons, "Users");
        addSectionButton(buttons, "Animals");
        addSectionButton(buttons, "Donations");
        addSectionButton(buttons, "Events");
        addSectionButton(buttons, "Adoptions");
        addActionButton(buttons, "Lookup tables", this::showLookupTables);
        addActionButton(buttons, "Add", this::add);
        addActionButton(buttons, "Edit", this::edit);
        addActionButton(buttons, "Delete", this::delete);
        addActionButton(buttons, "Donation tools", this::donationTools);
        addActionButton(buttons, "Event actions", this::eventActions);
        addActionButton(buttons, "Reports", this::showReport);
        addActionButton(buttons, "Communication", () -> CommunicationPanel.show(
                javax.swing.SwingUtilities.getWindowAncestor(this), model, database));
        addActionButton(buttons, "Notifications", () -> NotificationPanel.show(
                javax.swing.SwingUtilities.getWindowAncestor(this), admin));
        addActionButton(buttons, "Logout", logoutAction);
        header.add(buttons, BorderLayout.EAST);
        return header;
    }

    private void addSectionButton(JPanel panel, String name) {
        addActionButton(panel, name, () -> {
            section = name;
            refresh();
        });
    }

    private void addActionButton(JPanel panel, String name, Runnable action) {
        JButton button = new JButton(name);
        button.addActionListener(event -> action.run());
        panel.add(button);
    }

    private void refresh() {
        sectionLabel.setText("Admin: " + admin.getName() + " | " + section);
        userFilters.setVisible("Users".equals(section));
        if ("Users".equals(section)) {
            setColumns("ID", "Role", "Name", "Email", "Phone");
            String query = userSearchField.getText().trim().toLowerCase(java.util.Locale.ROOT);
            String role = (String) userRoleFilter.getSelectedItem();
            for (User user : model.getAllUsers()) {
                if (!"All roles".equals(role) && !role.equals(user.getRole())) continue;
                if (!query.isEmpty() && !(String.valueOf(user.getId()).contains(query)
                        || user.getName().toLowerCase(java.util.Locale.ROOT).contains(query)
                        || user.getEmail().toLowerCase(java.util.Locale.ROOT).contains(query)
                        || user.getPhone().toLowerCase(java.util.Locale.ROOT).contains(query))) continue;
                addRow(user.getId(), user.getRole(), user.getName(), user.getEmail(), user.getPhone());
            }
        } else if ("Animals".equals(section)) {
            setColumns("ID", "Name", "Species", "Breed", "Age", "Status");
            for (Animal animal : model.getAllAnimals()) {
                addRow(animal.getId(), animal.getName(), animal.getSpecies(), animal.getBreed(),
                        animal.getAge(), animal.getStatus());
            }
        } else if ("Adoptions".equals(section)) {
            setColumns("ID", "Adopter", "Animal", "Status", "Package");
            for (AdoptionRequest request : model.getAllAdoptions()) {
                addRow(request.getId(), request.getAdopter().getName(), request.getAnimal().getName(),
                        request.getStatus(), request.getAdoptionPackage().getDescription());
            }
        } else if ("Donations".equals(section)) {
            setColumns("ID", "Date", "Donor", "Type", "Amount", "Payment");
            model.getAllDonations().forEach(d -> addRow(d.getId(), d.getDate(), d.getDonor().getName(),
                    d.getType(), d.getAmount(), d.getPaymentMethod()));
        } else if ("Events".equals(section)) {
            setColumns("ID", "Title", "Category", "Date", "Location", "Capacity", "Registered", "Attended");
            model.getAllEvents().forEach(e -> addRow(e.getId(), e.getTitle(), e.getCategory(), e.getDate(),
                    e.getLocation(), e.getCapacity(), e.getRegisteredUsers().size(), e.getAttendedCount()));
        } else {
            setColumns("ID", "Details");
            model.getAllEvents().forEach(e -> addRow(e.getId(), e.toString()));
        }
    }

    private void setColumns(String... columns) {
        tableModel.setRowCount(0);
        tableModel.setColumnIdentifiers(columns);
    }

    private void addRow(Object... values) {
        tableModel.addRow(values);
    }

    private int selectedId() {
        int row = table.getSelectedRow();
        if (row < 0) {
            throw new IllegalArgumentException("Select a row first.");
        }
        return ((Number) tableModel.getValueAt(row, 0)).intValue();
    }

    private void add() {
        try {
            if ("Users".equals(section)) {
                AbstractUserFactory userFactory = (AbstractUserFactory) JOptionPane.showInputDialog(this,
                        "Role", "Add user", JOptionPane.PLAIN_MESSAGE, null,
                        userFactories.toArray(new AbstractUserFactory[0]), userFactories.get(0));
                if (userFactory == null) return;
                String name = required("Name");
                String email = required("Email");
                validateEmail(email, -1);
                String phone = required("Phone");
                if (!Validator.isValidPhone(phone)) {
                    throw new IllegalArgumentException(Validator.PHONE_RULE);
                }
                String passwordHash = null;
                if (!"Volunteer".equals(userFactory.getUserType())) {
                    passwordHash = promptPasswordHash();
                    if (passwordHash == null) return;
                }
                NotificationChannelSelector channelSelector =
                        new NotificationChannelSelector(notificationFactories);
                int channelChoice = JOptionPane.showConfirmDialog(this, channelSelector,
                        "Choose notification channels", JOptionPane.OK_CANCEL_OPTION,
                        JOptionPane.PLAIN_MESSAGE);
                if (channelChoice != JOptionPane.OK_OPTION) return;
                User user = userFactory.createUser(model.nextUserId(), name, email, phone,
                        channelSelector.getSelectedChannels());
                if (passwordHash != null) {
                    user.setPasswordHash(passwordHash);
                }
                model.addUser(user);
            } else if ("Animals".equals(section)) {
                String name = required("Name");
                String species = (String) JOptionPane.showInputDialog(this, "Species", "Add animal",
                        JOptionPane.PLAIN_MESSAGE, null, model.getSpecies().getAll().toArray(), "Dog");
                if (species == null) return;
                String breed = required("Breed");
                int age = Integer.parseInt(required("Age"));
                Animal animal = new Animal(model.nextAnimalId(), name, species, breed, age, "Healthy");
                animal.setNeeds(required("Needs"));
                model.addAnimal(animal);
            } else if ("Donations".equals(section)) {
                createDonation();
            } else if ("Events".equals(section)) {
                createEvent();
            } else {
                throw new IllegalArgumentException("Adding records for this section will be added next.");
            }
            saveAndRefresh();
        } catch (RuntimeException exception) {
            showError(exception);
        }
    }

    private String promptPasswordHash() {
        JPasswordField passwordField = new JPasswordField(20);
        JPasswordField confirmationField = new JPasswordField(20);
        JPanel fields = new JPanel(new GridLayout(0, 2, 8, 6));
        fields.add(new JLabel("Password (at least 6 characters):"));
        fields.add(passwordField);
        fields.add(new JLabel("Confirm password:"));
        fields.add(confirmationField);
        if (JOptionPane.showConfirmDialog(this, fields, "Set account password",
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE) != JOptionPane.OK_OPTION) {
            passwordField.setText("");
            confirmationField.setText("");
            return null;
        }

        char[] password = passwordField.getPassword();
        char[] confirmation = confirmationField.getPassword();
        try {
            if (password.length < 6) {
                throw new IllegalArgumentException("Password must contain at least 6 characters.");
            }
            if (!Arrays.equals(password, confirmation)) {
                throw new IllegalArgumentException("Passwords do not match.");
            }
            return AuthenticationService.hash(password);
        } finally {
            Arrays.fill(password, '\0');
            Arrays.fill(confirmation, '\0');
            passwordField.setText("");
            confirmationField.setText("");
        }
    }

    private void edit() {
        try {
            int id = selectedId();
            if ("Users".equals(section)) {
                editUser(model.getUser(id));
            } else if ("Animals".equals(section)) {
                Animal animal = model.getAnimal(id);
                animal.setName(required("Name [" + animal.getName() + "]"));
                animal.setBreed(required("Breed [" + animal.getBreed() + "]"));
                animal.setStatus((String) JOptionPane.showInputDialog(this, "Status", "Edit animal",
                        JOptionPane.PLAIN_MESSAGE, null, Animal.STATUSES, animal.getStatus()));
            } else if ("Adoptions".equals(section)) {
                AdoptionRequest request = model.getAdoption(id);
                if ("Pending".equals(request.getStatus())) {
                    String[] choices = {"Approved", "Rejected"};
                    String status = (String) JOptionPane.showInputDialog(this, "Decision", "Adoption request",
                            JOptionPane.PLAIN_MESSAGE, null, choices, choices[0]);
                    if ("Approved".equals(status)) {
                        request.approve();
                        showNotification(request.getAdopter().getName(),
                                "Your adoption of " + request.getAnimal().getName() + " was approved! Total: "
                                        + request.getAdoptionPackage().getCost() + " EGP");
                    } else if ("Rejected".equals(status)) {
                        request.reject();
                        showNotification(request.getAdopter().getName(),
                                "Sorry, your adoption request for " + request.getAnimal().getName() + " was rejected.");
                    }
                }
            } else if ("Donations".equals(section)) {
                editDonation(model.getDonation(id));
            } else if ("Events".equals(section)) {
                editEvent(model.getEvent(id));
            } else {
                throw new IllegalArgumentException("Edit is not available for this section yet.");
            }
            saveAndRefresh();
        } catch (RuntimeException exception) {
            showError(exception);
        }
    }

    private void editUser(User user) {
        if (user == null) {
            throw new IllegalArgumentException("The selected user no longer exists.");
        }

        JTextField nameField = new JTextField(user.getName(), 24);
        JTextField emailField = new JTextField(user.getEmail(), 24);
        JTextField phoneField = new JTextField(user.getPhone(), 24);
        NotificationChannelSelector channelSelector =
                new NotificationChannelSelector(notificationFactories, user.getPreferredChannels());

        JPanel fields = new JPanel(new GridLayout(0, 2, 8, 6));
        fields.add(new JLabel("Name:"));
        fields.add(nameField);
        fields.add(new JLabel("Email:"));
        fields.add(emailField);
        fields.add(new JLabel("Phone:"));
        fields.add(phoneField);

        JComboBox<String> skillBox = null;
        JTextField availabilityField = null;
        JTextField hoursField = null;
        JComboBox<String> homeTypeBox = null;
        if (user instanceof Volunteer) {
            Volunteer volunteer = (Volunteer) user;
            skillBox = comboWithCurrentValue(model.getVolunteerSkills().getAll(), volunteer.getSkill());
            availabilityField = new JTextField(volunteer.getAvailability(), 24);
            fields.add(new JLabel("Skill:"));
            fields.add(skillBox);
            fields.add(new JLabel("Availability:"));
            fields.add(availabilityField);
            hoursField = new JTextField(Double.toString(volunteer.getHoursWorked()), 24);
            fields.add(new JLabel("Total hours worked:"));
            fields.add(hoursField);
        } else if (user instanceof Adopter) {
            homeTypeBox = comboWithCurrentValue(java.util.Arrays.asList(HOME_TYPES),
                    ((Adopter) user).getHomeType());
            fields.add(new JLabel("Home type:"));
            fields.add(homeTypeBox);
        }

        fields.add(new JLabel("Notification channels:"));
        fields.add(channelSelector);
        if (JOptionPane.showConfirmDialog(this, fields, "Edit " + user.getRole(),
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE) != JOptionPane.OK_OPTION) {
            return;
        }

        String name = nameField.getText().trim();
        if (name.isEmpty()) {
            throw new IllegalArgumentException("Name cannot be empty.");
        }
        String email = emailField.getText().trim();
        validateEmail(email, user.getId());
        String phone = phoneField.getText().trim();
        if (!Validator.isValidPhone(phone)) {
            throw new IllegalArgumentException(Validator.PHONE_RULE);
        }
        List<String> channels = channelSelector.getSelectedChannels();

        Double volunteerHours = null;
        if (user instanceof Volunteer) {
            try {
                volunteerHours = Double.parseDouble(hoursField.getText().trim());
            } catch (NumberFormatException exception) {
                throw new IllegalArgumentException("Volunteer hours must be a finite nonnegative number.");
            }
            if (!Double.isFinite(volunteerHours) || volunteerHours < 0) {
                throw new IllegalArgumentException("Volunteer hours must be a finite nonnegative number.");
            }
        }
        user.setName(name);
        user.setEmail(email);
        user.setPhone(phone);
        user.setPreferredChannels(channels);
        if (user instanceof Volunteer) {
            Volunteer volunteer = (Volunteer) user;
            volunteer.setSkill((String) skillBox.getSelectedItem());
            volunteer.setAvailability(availabilityField.getText().trim());
            volunteer.setHoursWorked(volunteerHours);
        } else if (user instanceof Adopter) {
            ((Adopter) user).setHomeType((String) homeTypeBox.getSelectedItem());
        }
    }

    private JComboBox<String> comboWithCurrentValue(List<String> values, String currentValue) {
        JComboBox<String> combo = new JComboBox<>(values.toArray(new String[0]));
        if (!values.contains(currentValue)) {
            combo.addItem(currentValue);
        }
        combo.setSelectedItem(currentValue);
        return combo;
    }

    private void validateEmail(String email, int userIdBeingEdited) {
        if (!Validator.isValidEmail(email)) {
            throw new IllegalArgumentException(Validator.EMAIL_RULE);
        }
        for (User existing : model.getAllUsers()) {
            if (existing.getId() != userIdBeingEdited && existing.getEmail().equalsIgnoreCase(email)) {
                throw new IllegalArgumentException("An account with this email already exists.");
            }
        }
    }

    private void delete() {
        try {
            int id = selectedId();
            if (JOptionPane.showConfirmDialog(this, "Delete selected record?", "Confirm",
                    JOptionPane.YES_NO_OPTION) != JOptionPane.YES_OPTION) return;
            if ("Users".equals(section)) {
                User user = model.getUser(id);
                for (Donation donation : model.getAllDonations()) {
                    if (donation.getDonor() == user) {
                        throw new IllegalArgumentException("This user has donation records and cannot be deleted.");
                    }
                }
                for (AdoptionRequest request : model.getAllAdoptions()) {
                    if (request.getAdopter() == user) {
                        throw new IllegalArgumentException("This user has adoption requests and cannot be deleted.");
                    }
                }
                for (Event event : model.getAllEvents()) {
                    event.removeUser(user);
                }
                model.deleteUser(id);
            } else if ("Animals".equals(section)) {
                for (AdoptionRequest request : model.getAllAdoptions()) {
                    if (request.getAnimal() == model.getAnimal(id)) {
                        throw new IllegalArgumentException("This animal has adoption requests and cannot be deleted.");
                    }
                }
                model.deleteAnimal(id);
            }
            else if ("Adoptions".equals(section)) model.deleteAdoption(id);
            else if ("Donations".equals(section)) {
                Donation donation = model.getDonation(id);
                donation.getDonor().addToTotal(-donation.getAmount());
                model.deleteDonation(id);
            } else if ("Events".equals(section)) {
                Event event = model.getEvent(id);
                event.notifyObservers("Sorry, \"" + event.getTitle() + "\" on " + event.getDate()
                        + " was cancelled.");
                model.deleteEvent(id);
            }
            else throw new IllegalArgumentException("Delete is not available for this section.");
            saveAndRefresh();
        } catch (RuntimeException exception) {
            showError(exception);
        }
    }

    private void createDonation() {
        Donor donor = chooseDonor();
        if (donor == null) return;
        String type = chooseValue("Donation type", model.getDonationTypes().getAll(), "Money");
        if (type == null) return;
        double amount = positiveAmount("Amount or estimated value (EGP)");
        String paymentMethod = "In-kind";
        if ("Money".equals(type)) {
            AbstractPaymentFactory paymentFactory = (AbstractPaymentFactory) JOptionPane.showInputDialog(this,
                    "Payment method", "Donation", JOptionPane.PLAIN_MESSAGE, null,
                    paymentFactories.toArray(), paymentFactories.get(0));
            if (paymentFactory == null) return;
            PaymentStrategy payment = paymentFactory.createPayment();
            String[] details = new String[payment.getRequiredDetails().length];
            String[] fields = payment.getRequiredDetails();
            for (int i = 0; i < fields.length; i++) {
                details[i] = required(fields[i]);
                if (!payment.isValidDetail(i, details[i])) {
                    throw new IllegalArgumentException("Invalid " + fields[i] + ".");
                }
            }
            JOptionPane.showMessageDialog(this, payment.pay(donor.getName(), amount, details),
                    "Payment", JOptionPane.INFORMATION_MESSAGE);
            paymentMethod = payment.getReference(details);
        }
        Donation donation = new Donation(model.nextDonationId(), donor, type, amount, paymentMethod);
        model.addDonation(donation);
        donor.addToTotal(amount);
        donation.attach(donor);
        for (User user : model.getUsersByRole("Admin")) donation.attach(user);
        donation.notifyObservers("Donation #" + donation.getId() + " received: " + type
                + ", " + amount + " EGP from " + donor.getName() + ". Thank you!");
        saveAndRefresh();
        JOptionPane.showMessageDialog(this, donation.generateReceipt(), "Donation receipt",
                JOptionPane.INFORMATION_MESSAGE);
    }

    private void editDonation(Donation donation) {
        if (donation == null) throw new IllegalArgumentException("The selected donation no longer exists.");
        JTextField amountField = new JTextField(Double.toString(donation.getAmount()), 16);
        JComboBox<String> typeBox = comboWithCurrentValue(model.getDonationTypes().getAll(), donation.getType());
        JPanel fields = new JPanel(new GridLayout(0, 2, 8, 6));
        fields.add(new JLabel("Type:"));
        fields.add(typeBox);
        fields.add(new JLabel("Amount / estimated value (EGP):"));
        fields.add(amountField);
        if (JOptionPane.showConfirmDialog(this, fields, "Edit donation #" + donation.getId(),
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE) != JOptionPane.OK_OPTION) return;
        double amount = parsePositiveAmount(amountField.getText());
        donation.getDonor().addToTotal(amount - donation.getAmount());
        donation.setAmount(amount);
        donation.setType((String) typeBox.getSelectedItem());
    }

    private void createEvent() {
        JTextField title = new JTextField(22);
        JComboBox<String> category = new JComboBox<>(model.getEventCategories().getAll().toArray(new String[0]));
        JTextField date = new JTextField(22);
        JTextField location = new JTextField(22);
        JTextField capacity = new JTextField("50", 22);
        JComboBox<String> skill = eventSkillBox("Any");
        JPanel fields = eventFields(title, category, date, location, capacity, skill);
        if (JOptionPane.showConfirmDialog(this, fields, "Create event",
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE) != JOptionPane.OK_OPTION) return;
        int seats = parseCapacity(capacity.getText(), 1);
        Event event = new Event(model.nextEventId(), nonblank(title.getText(), "Title"),
                (String) category.getSelectedItem(), nonblank(date.getText(), "Date"),
                nonblank(location.getText(), "Location"), seats);
        event.setRequiredSkill((String) skill.getSelectedItem());
        model.addEvent(event);
    }

    private void editEvent(Event event) {
        if (event == null) throw new IllegalArgumentException("The selected event no longer exists.");
        JTextField title = new JTextField(event.getTitle(), 22);
        JComboBox<String> category = comboWithCurrentValue(model.getEventCategories().getAll(), event.getCategory());
        JTextField date = new JTextField(event.getDate(), 22);
        JTextField location = new JTextField(event.getLocation(), 22);
        JTextField capacity = new JTextField(Integer.toString(event.getCapacity()), 22);
        JComboBox<String> skill = eventSkillBox(event.getRequiredSkill());
        if (JOptionPane.showConfirmDialog(this, eventFields(title, category, date, location, capacity, skill),
                "Edit event #" + event.getId(), JOptionPane.OK_CANCEL_OPTION,
                JOptionPane.PLAIN_MESSAGE) != JOptionPane.OK_OPTION) return;
        int seats = parseCapacity(capacity.getText(), event.getRegisteredUsers().size());
        event.setTitle(nonblank(title.getText(), "Title"));
        event.setCategory((String) category.getSelectedItem());
        event.setDate(nonblank(date.getText(), "Date"));
        event.setLocation(nonblank(location.getText(), "Location"));
        event.setCapacity(seats);
        event.setRequiredSkill((String) skill.getSelectedItem());
        event.notifyObservers("Update for \"" + event.getTitle() + "\": now on " + event.getDate()
                + " at " + event.getLocation() + ".");
    }

    private JPanel eventFields(JTextField title, JComboBox<String> category, JTextField date,
            JTextField location, JTextField capacity, JComboBox<String> skill) {
        JPanel fields = new JPanel(new GridLayout(0, 2, 8, 6));
        fields.add(new JLabel("Title:")); fields.add(title);
        fields.add(new JLabel("Category:")); fields.add(category);
        fields.add(new JLabel("Date (e.g. 2026-11-20):")); fields.add(date);
        fields.add(new JLabel("Location:")); fields.add(location);
        fields.add(new JLabel("Capacity:")); fields.add(capacity);
        fields.add(new JLabel("Volunteer skill needed:")); fields.add(skill);
        return fields;
    }

    private JComboBox<String> eventSkillBox(String currentSkill) {
        java.util.ArrayList<String> skills = new java.util.ArrayList<>();
        skills.add("Any");
        skills.addAll(model.getVolunteerSkills().getAll());
        return comboWithCurrentValue(skills, currentSkill);
    }

    private int parseCapacity(String value, int minimum) {
        try {
            int capacity = Integer.parseInt(value.trim());
            if (capacity < Math.max(1, minimum) || capacity > 10000) {
                throw new IllegalArgumentException("Capacity must be between " + Math.max(1, minimum) + " and 10000.");
            }
            return capacity;
        } catch (NumberFormatException exception) {
            throw new IllegalArgumentException("Capacity must be a whole number between 1 and 10000.");
        }
    }

    private String nonblank(String value, String label) {
        if (value == null || value.trim().isEmpty()) throw new IllegalArgumentException(label + " is required.");
        return value.trim();
    }

    private void donationTools() {
        try {
            String action = chooseValue("Donation report or receipt",
                    java.util.Arrays.asList("Donor history", "Annual tax statement", "Donation receipt"),
                    "Donor history");
            if (action == null) return;
            if ("Donation receipt".equals(action)) {
                Donation donation;
                if ("Donations".equals(section) && table.getSelectedRow() >= 0) {
                    donation = model.getDonation(selectedId());
                } else {
                    donation = chooseDonation();
                }
                if (donation != null) JOptionPane.showMessageDialog(this, donation.generateReceipt(),
                        "Donation receipt", JOptionPane.INFORMATION_MESSAGE);
                return;
            }
            Donor donor = chooseDonor();
            if (donor == null) return;
            StringBuilder report = new StringBuilder();
            report.append("Donor: ").append(donor.getName()).append(" | ").append(donor.getEmail()).append('\n');
            double money = 0;
            double inKind = 0;
            int year = LocalDate.now().getYear();
            for (Donation donation : model.getAllDonations()) {
                if (donation.getDonor() != donor) continue;
                if ("Annual tax statement".equals(action) && donation.getDate().getYear() != year) continue;
                report.append("R-").append(String.format("%04d", donation.getId())).append(" | ")
                        .append(donation.getDate()).append(" | ").append(donation.getType()).append(" | ")
                        .append(donation.getAmount()).append(" EGP | ").append(donation.getPaymentMethod()).append('\n');
                if (donation.isMoney()) money += donation.getAmount();
                else inKind += donation.getAmount();
            }
            double total = "Annual tax statement".equals(action)
                    ? money + inKind : donor.getTotalDonated();
            report.append("Annual tax statement".equals(action) ? "Donated this year: " : "Total donated: ")
                    .append(total).append(" EGP");
            if ("Annual tax statement".equals(action)) {
                report.insert(0, "YEARLY DONATION TAX STATEMENT " + year + "\n");
                report.append("\nMoney donations: ").append(money).append(" EGP")
                        .append("\nIn-kind estimated value: ").append(inKind).append(" EGP")
                        .append("\nNo goods or services were given in return for these donations.");
            }
            JOptionPane.showMessageDialog(this, report.toString(), action, JOptionPane.INFORMATION_MESSAGE);
        } catch (RuntimeException exception) {
            showError(exception);
        }
    }

    private void eventActions() {
        try {
            Event event = chooseEvent();
            if (event == null) return;
            String action = chooseValue("Event: " + event.getTitle(),
                    java.util.Arrays.asList("View attendees", "Register a user", "Assign volunteer",
                            "Mark attendance", "Send reminder", "Cancel event"), "View attendees");
            if (action == null) return;
            if ("View attendees".equals(action)) {
                StringBuilder details = new StringBuilder(event.toString()).append("\nRegistered users:\n");
                for (Observer observer : event.getRegisteredUsers()) {
                    User user = (User) observer;
                    details.append(event.getTicketNumber(user)).append(" | ")
                            .append(event.hasAttended(user) ? "attended" : "not yet").append(" | ")
                            .append(user.getName()).append(" (").append(user.getRole()).append(")\n");
                }
                details.append("\nVolunteer assignments:\n");
                for (shelter.model.VolunteerAssignment assignment : event.getAssignments()) {
                    details.append(assignment).append('\n');
                }
                JOptionPane.showMessageDialog(this, details.toString(), "Event details",
                        JOptionPane.INFORMATION_MESSAGE);
            } else if ("Register a user".equals(action)) {
                if (event.isFull()) throw new IllegalArgumentException("Sorry, this event is full.");
                User user = chooseUser();
                if (user == null) return;
                if (event.isRegistered(user)) throw new IllegalArgumentException("That user is already registered.");
                event.attach(user);
                String ticket = event.getTicketNumber(user);
                user.update("You are registered for \"" + event.getTitle() + "\" on " + event.getDate()
                        + ". Your ticket number is " + ticket + ".");
            } else if ("Assign volunteer".equals(action)) {
                java.util.ArrayList<Volunteer> volunteers = new java.util.ArrayList<>();
                for (User user : model.getUsersByRole("Volunteer")) volunteers.add((Volunteer) user);
                if (volunteers.isEmpty()) throw new IllegalArgumentException("There are no volunteers to assign.");
                JComboBox<Volunteer> selector = new JComboBox<>(volunteers.toArray(new Volunteer[0]));
                if (JOptionPane.showConfirmDialog(this, selector, "Choose volunteer",
                        JOptionPane.OK_CANCEL_OPTION) != JOptionPane.OK_OPTION) return;
                Volunteer volunteer = (Volunteer) selector.getSelectedItem();
                if (event.isAssigned(volunteer)) throw new IllegalArgumentException("Volunteer is already assigned.");
                boolean match = "Any".equals(event.getRequiredSkill())
                        || event.getRequiredSkill().equals(volunteer.getSkill());
                if (!match && JOptionPane.showConfirmDialog(this, "Skill does not match. Assign anyway?",
                        "Confirm assignment", JOptionPane.YES_NO_OPTION) != JOptionPane.YES_OPTION) return;
                String task = required("Assignment task");
                event.assignVolunteer(volunteer, task);
                volunteer.update("You are assigned to \"" + task + "\" at \"" + event.getTitle()
                        + "\" on " + event.getDate() + ".");
            } else if ("Mark attendance".equals(action)) {
                User user = chooseRegisteredUser(event);
                if (user == null) return;
                event.markAttended(user);
            } else if ("Send reminder".equals(action)) {
                if (event.getRegisteredUsers().isEmpty()) {
                    throw new IllegalArgumentException("Nobody is registered for this event yet.");
                }
                event.notifyObservers("Reminder: \"" + event.getTitle() + "\" is on " + event.getDate()
                        + " at " + event.getLocation() + ". See you there!");
            } else if ("Cancel event".equals(action)) {
                if (JOptionPane.showConfirmDialog(this, "Cancel \"" + event.getTitle() + "\"?",
                        "Confirm cancellation", JOptionPane.YES_NO_OPTION) != JOptionPane.YES_OPTION) return;
                event.notifyObservers("Sorry, \"" + event.getTitle() + "\" on " + event.getDate()
                        + " was cancelled.");
                model.deleteEvent(event.getId());
            }
            saveAndRefresh();
        } catch (RuntimeException exception) {
            showError(exception);
        }
    }

    private Donor chooseDonor() {
        java.util.ArrayList<Donor> donors = new java.util.ArrayList<>();
        for (User user : model.getUsersByRole("Donor")) donors.add((Donor) user);
        if (donors.isEmpty()) throw new IllegalArgumentException("No donors are available.");
        JComboBox<Donor> selector = new JComboBox<>(donors.toArray(new Donor[0]));
        if (JOptionPane.showConfirmDialog(this, selector, "Choose donor",
                JOptionPane.OK_CANCEL_OPTION) != JOptionPane.OK_OPTION) return null;
        return (Donor) selector.getSelectedItem();
    }

    private User chooseUser() {
        List<User> users = model.getAllUsers();
        if (users.isEmpty()) throw new IllegalArgumentException("No users are available.");
        JComboBox<User> selector = new JComboBox<>(users.toArray(new User[0]));
        if (JOptionPane.showConfirmDialog(this, selector, "Choose user",
                JOptionPane.OK_CANCEL_OPTION) != JOptionPane.OK_OPTION) return null;
        return (User) selector.getSelectedItem();
    }

    private User chooseRegisteredUser(Event event) {
        java.util.ArrayList<User> users = new java.util.ArrayList<>();
        for (Observer observer : event.getRegisteredUsers()) users.add((User) observer);
        if (users.isEmpty()) throw new IllegalArgumentException("Nobody is registered for this event.");
        JComboBox<User> selector = new JComboBox<>(users.toArray(new User[0]));
        if (JOptionPane.showConfirmDialog(this, selector, "Mark attendance",
                JOptionPane.OK_CANCEL_OPTION) != JOptionPane.OK_OPTION) return null;
        return (User) selector.getSelectedItem();
    }

    private Event chooseEvent() {
        if ("Events".equals(section) && table.getSelectedRow() >= 0) return model.getEvent(selectedId());
        List<Event> events = model.getAllEvents();
        if (events.isEmpty()) throw new IllegalArgumentException("No events are available.");
        JComboBox<Event> selector = new JComboBox<>(events.toArray(new Event[0]));
        if (JOptionPane.showConfirmDialog(this, selector, "Choose event",
                JOptionPane.OK_CANCEL_OPTION) != JOptionPane.OK_OPTION) return null;
        return (Event) selector.getSelectedItem();
    }

    private Donation chooseDonation() {
        List<Donation> donations = model.getAllDonations();
        if (donations.isEmpty()) throw new IllegalArgumentException("No donations are available.");
        JComboBox<Donation> selector = new JComboBox<>(donations.toArray(new Donation[0]));
        if (JOptionPane.showConfirmDialog(this, selector, "Choose donation",
                JOptionPane.OK_CANCEL_OPTION) != JOptionPane.OK_OPTION) return null;
        return (Donation) selector.getSelectedItem();
    }

    private String chooseValue(String title, List<String> values, String defaultValue) {
        if (values.isEmpty()) throw new IllegalArgumentException("No choices are available.");
        return (String) JOptionPane.showInputDialog(this, title, title, JOptionPane.PLAIN_MESSAGE,
                null, values.toArray(new String[0]), defaultValue);
    }

    private double positiveAmount(String label) {
        return parsePositiveAmount(required(label));
    }

    private double parsePositiveAmount(String value) {
        try {
            double amount = Double.parseDouble(value.trim());
            if (!Double.isFinite(amount) || amount <= 0) throw new IllegalArgumentException("Amount must be finite and greater than zero.");
            return amount;
        } catch (NumberFormatException exception) {
            throw new IllegalArgumentException("Amount must be a finite number greater than zero.");
        }
    }

    private void showReport() {
        JOptionPane.showMessageDialog(this, "Animals: " + model.getAllAnimals().size()
                + "\nUsers: " + model.getAllUsers().size()
                + "\nDonations: " + model.getAllDonations().size()
                + "\nEvents: " + model.getAllEvents().size()
                + "\nAdoptions: " + model.getAllAdoptions().size(), "Shelter report",
                JOptionPane.INFORMATION_MESSAGE);
    }

    private void showLookupTables() {
        JDialog dialog = new JDialog(javax.swing.SwingUtilities.getWindowAncestor(this),
                "Lookup table management", java.awt.Dialog.ModalityType.APPLICATION_MODAL);
        dialog.setLayout(new BorderLayout(10, 10));

        JComboBox<shelter.model.LookupTable> selector = new JComboBox<>(
                model.getAllLookupTables().toArray(new shelter.model.LookupTable[0]));
        DefaultListModel<String> valuesModel = new DefaultListModel<>();
        JList<String> valuesList = new JList<>(valuesModel);
        valuesList.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);

        Runnable refreshValues = () -> {
            valuesModel.clear();
            shelter.model.LookupTable table = (shelter.model.LookupTable) selector.getSelectedItem();
            if (table != null) {
                for (String value : table.getAll()) {
                    valuesModel.addElement(value);
                }
            }
        };
        selector.addActionListener(event -> refreshValues.run());

        JPanel actions = new JPanel(new GridLayout(1, 3, 5, 5));
        JButton addButton = new JButton("Add");
        addButton.addActionListener(event -> {
            shelter.model.LookupTable table = (shelter.model.LookupTable) selector.getSelectedItem();
            String value = JOptionPane.showInputDialog(dialog, "New value:");
            if (value != null && !value.trim().isEmpty()) {
                if (!table.add(value.trim())) {
                    showError(new IllegalArgumentException("The value is empty or already exists."));
                    return;
                }
                saveAndRefresh();
                refreshValues.run();
            }
        });
        JButton updateButton = new JButton("Update");
        updateButton.addActionListener(event -> {
            int index = valuesList.getSelectedIndex();
            shelter.model.LookupTable table = (shelter.model.LookupTable) selector.getSelectedItem();
            if (index < 0 || table == null) {
                showError(new IllegalArgumentException("Select a value first."));
                return;
            }
            String value = JOptionPane.showInputDialog(dialog, "New value:", valuesList.getSelectedValue());
            if (value != null && !value.trim().isEmpty()) {
                if (table.getAll().contains(value.trim()) && !value.trim().equals(valuesList.getSelectedValue())) {
                    showError(new IllegalArgumentException("The value already exists."));
                    return;
                }
                if (!table.update(index, value.trim())) {
                    showError(new IllegalArgumentException("The value could not be updated."));
                    return;
                }
                saveAndRefresh();
                refreshValues.run();
            }
        });
        JButton deleteButton = new JButton("Delete");
        deleteButton.addActionListener(event -> {
            int index = valuesList.getSelectedIndex();
            shelter.model.LookupTable table = (shelter.model.LookupTable) selector.getSelectedItem();
            if (index < 0 || table == null) {
                showError(new IllegalArgumentException("Select a value first."));
                return;
            }
            if (JOptionPane.showConfirmDialog(dialog, "Delete selected value?", "Confirm",
                    JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
                table.delete(index);
                saveAndRefresh();
                refreshValues.run();
            }
        });
        actions.add(addButton);
        actions.add(updateButton);
        actions.add(deleteButton);

        JPanel top = new JPanel(new BorderLayout(5, 5));
        top.add(new JLabel("Lookup table:"), BorderLayout.WEST);
        top.add(selector, BorderLayout.CENTER);
        dialog.add(top, BorderLayout.NORTH);
        dialog.add(new JScrollPane(valuesList), BorderLayout.CENTER);
        dialog.add(actions, BorderLayout.SOUTH);
        refreshValues.run();
        dialog.setSize(420, 320);
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
    }

    private void showNotification(String recipient, String message) {
        JOptionPane.showMessageDialog(this,
                "Notification sent to " + recipient + ":\n\n" + message,
                "Notification", JOptionPane.INFORMATION_MESSAGE);
    }

    private String required(String label) {
        String value = JOptionPane.showInputDialog(this, label);
        if (value == null || value.trim().isEmpty()) throw new IllegalArgumentException(label + " is required.");
        return value.trim();
    }

    private void saveAndRefresh() {
        database.save(model);
        refresh();
    }

    private void showError(RuntimeException exception) {
        JOptionPane.showMessageDialog(this, exception.getMessage(), "Operation failed",
                JOptionPane.ERROR_MESSAGE);
    }
}
