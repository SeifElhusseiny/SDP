package shelter.view;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableModel;

import shelter.database.JsonDatabase;
import shelter.model.AdoptionRequest;
import shelter.model.Animal;
import shelter.model.Adopter;
import shelter.model.Donation;
import shelter.model.ShelterModel;
import shelter.model.User;
import shelter.patterns.factory.UserFactory;

public class AdminDashboardPanel extends JPanel {
    private final ShelterModel model;
    private final JsonDatabase database;
    private final User admin;
    private final Runnable logoutAction;
    private final DefaultTableModel tableModel = new DefaultTableModel();
    private final JTable table = new JTable(tableModel);
    private final JLabel sectionLabel = new JLabel("Users");
    private String section = "Users";

    public AdminDashboardPanel(ShelterModel model, JsonDatabase database, User admin, Runnable logoutAction) {
        this.model = model;
        this.database = database;
        this.admin = admin;
        this.logoutAction = logoutAction;
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(18, 18, 18, 18));
        add(createHeader(), BorderLayout.NORTH);
        add(new JScrollPane(table), BorderLayout.CENTER);
        refresh();
    }

    private JPanel createHeader() {
        JPanel header = new JPanel(new BorderLayout());
        sectionLabel.setText("Admin: " + admin.getName() + " | Users");
        header.add(sectionLabel, BorderLayout.WEST);
        JPanel buttons = new JPanel(new GridLayout(3, 4, 5, 5));
        addSectionButton(buttons, "Users");
        addSectionButton(buttons, "Animals");
        addSectionButton(buttons, "Donations");
        addSectionButton(buttons, "Events");
        addSectionButton(buttons, "Adoptions");
        addActionButton(buttons, "Add", this::add);
        addActionButton(buttons, "Edit", this::edit);
        addActionButton(buttons, "Delete", this::delete);
        addActionButton(buttons, "Reports", this::showReport);
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
        if ("Users".equals(section)) {
            setColumns("ID", "Role", "Name", "Email", "Phone");
            for (User user : model.getAllUsers()) {
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
            setColumns("ID", "Donor", "Type", "Amount", "Payment");
            model.getAllDonations().forEach(d -> addRow(d.getId(), d.getDonor().getName(), d.getType(),
                    d.getAmount(), d.getPaymentMethod()));
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
                String role = (String) JOptionPane.showInputDialog(this, "Role", "Add user",
                        JOptionPane.PLAIN_MESSAGE, null, UserFactory.USER_TYPES, "Volunteer");
                if (role == null) return;
                String name = required("Name");
                String email = required("Email");
                String phone = required("Phone");
                User user = UserFactory.createUser(role, model.nextUserId(), name, email, phone,
                        new ArrayList<>(Arrays.asList("Email")));
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
            } else {
                throw new IllegalArgumentException("Adding records for this section will be added next.");
            }
            saveAndRefresh();
        } catch (RuntimeException exception) {
            showError(exception);
        }
    }

    private void edit() {
        try {
            int id = selectedId();
            if ("Animals".equals(section)) {
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
            } else {
                throw new IllegalArgumentException("Edit is not available for this section yet.");
            }
            saveAndRefresh();
        } catch (RuntimeException exception) {
            showError(exception);
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
            else throw new IllegalArgumentException("Delete is not available for this section.");
            saveAndRefresh();
        } catch (RuntimeException exception) {
            showError(exception);
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
