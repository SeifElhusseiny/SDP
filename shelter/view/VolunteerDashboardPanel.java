package shelter.view;

import java.awt.BorderLayout;
import java.awt.GridLayout;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

import shelter.database.JsonDatabase;
import shelter.model.Animal;
import shelter.model.CareRecord;
import shelter.model.Event;
import shelter.model.ShelterModel;
import shelter.model.User;
import shelter.model.Volunteer;

public class VolunteerDashboardPanel extends JPanel {
    private final ShelterModel model;
    private final JsonDatabase database;
    private final Volunteer volunteer;
    private final DefaultTableModel tableModel = new DefaultTableModel();
    private final JTable table = new JTable(tableModel);

    public VolunteerDashboardPanel(ShelterModel model, JsonDatabase database, Volunteer volunteer,
            Runnable logoutAction) {
        this.model = model;
        this.database = database;
        this.volunteer = volunteer;
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(18, 18, 18, 18));
        JPanel actions = new JPanel(new GridLayout(2, 4, 8, 8));
        addButton(actions, "View animals", this::showAnimals);
        addButton(actions, "Record care", this::recordCare);
        addButton(actions, "Assigned events", this::showEvents);
        addButton(actions, "Volunteer hours", this::logHours);
        addButton(actions, "Certificate", this::certificate);
        addButton(actions, "My profile", this::showProfile);
        addButton(actions, "Notifications", () -> showMessage("Notifications are delivered through preferred channels."));
        addButton(actions, "Logout", logoutAction);
        add(actions, BorderLayout.NORTH);
        add(new JScrollPane(table), BorderLayout.CENTER);
        showAnimals();
    }

    private void addButton(JPanel panel, String text, Runnable action) {
        JButton button = new JButton(text);
        button.addActionListener(event -> action.run());
        panel.add(button);
    }

    private void showAnimals() {
        tableModel.setColumnIdentifiers(new Object[] {"ID", "Name", "Species", "Health", "Status", "Needs"});
        tableModel.setRowCount(0);
        for (Animal animal : model.getAllAnimals()) {
            tableModel.addRow(new Object[] {animal.getId(), animal.getName(), animal.getSpecies(),
                    animal.getHealthStatus(), animal.getStatus(), animal.getNeeds()});
        }
    }

    private void recordCare() {
        try {
            int id = Integer.parseInt(required("Animal ID"));
            Animal animal = model.getAnimal(id);
            if (animal == null) throw new IllegalArgumentException("No animal with that ID.");
            String item = required("Care/resource type");
            int quantity = Integer.parseInt(required("Quantity"));
            if (quantity <= 0) throw new IllegalArgumentException("Quantity must be positive.");
            animal.addCareRecord(new CareRecord(item, quantity));
            database.save(model);
            showMessage("Care recorded for " + animal.getName() + ".");
        } catch (RuntimeException exception) {
            showMessage(exception.getMessage());
        }
    }

    private void showEvents() {
        tableModel.setColumnIdentifiers(new Object[] {"ID", "Title", "Date", "Task"});
        tableModel.setRowCount(0);
        for (Event event : model.getAllEvents()) {
            for (shelter.model.VolunteerAssignment assignment : event.getAssignments()) {
                if (assignment.getVolunteer() == volunteer) {
                    tableModel.addRow(new Object[] {event.getId(), event.getTitle(), event.getDate(),
                            assignment.getTask()});
                }
            }
        }
    }

    private void logHours() {
        try {
            double hours = Double.parseDouble(required("Hours worked"));
            if (hours <= 0) throw new IllegalArgumentException("Hours must be positive.");
            volunteer.logHours(hours);
            database.save(model);
            showMessage("Volunteer hours updated.");
        } catch (RuntimeException exception) {
            showMessage(exception.getMessage());
        }
    }

    private void certificate() {
        if (volunteer.getHoursWorked() == 0) {
            showMessage("Log volunteer hours before generating a certificate.");
            return;
        }
        showMessage(volunteer.generateCertificate());
    }

    private void showProfile() {
        showMessage("Name: " + volunteer.getName() + "\nEmail: " + volunteer.getEmail()
                + "\nSkill: " + volunteer.getSkill() + "\nAvailability: " + volunteer.getAvailability()
                + "\nHours: " + volunteer.getHoursWorked());
    }

    private String required(String label) {
        String value = JOptionPane.showInputDialog(this, label);
        if (value == null || value.trim().isEmpty()) throw new IllegalArgumentException(label + " is required.");
        return value.trim();
    }

    private void showMessage(String message) {
        JOptionPane.showMessageDialog(this, message, "Paws & Whiskers", JOptionPane.INFORMATION_MESSAGE);
    }
}
