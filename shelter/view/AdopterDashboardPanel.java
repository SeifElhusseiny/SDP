package shelter.view;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.util.ArrayList;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

import shelter.database.JsonDatabase;
import shelter.model.Adopter;
import shelter.model.AdoptionRequest;
import shelter.model.Animal;
import shelter.model.ShelterModel;
import shelter.patterns.factory.PackageFactory;

public class AdopterDashboardPanel extends JPanel {
    private final ShelterModel model;
    private final JsonDatabase database;
    private final Adopter adopter;
    private final DefaultTableModel tableModel = new DefaultTableModel();
    private final JTable table = new JTable(tableModel);

    public AdopterDashboardPanel(ShelterModel model, JsonDatabase database, Adopter adopter, Runnable logoutAction) {
        this.model = model;
        this.database = database;
        this.adopter = adopter;
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(18, 18, 18, 18));
        JPanel actions = new JPanel(new GridLayout(1, 4, 8, 8));
        addButton(actions, "Available animals", this::showAnimals);
        addButton(actions, "Adoption request", this::createRequest);
        addButton(actions, "My requests", this::showRequests);
        addButton(actions, "My profile", this::showProfile);
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
        tableModel.setColumnIdentifiers(new Object[] {"ID", "Name", "Species", "Breed", "Age", "Health"});
        tableModel.setRowCount(0);
        for (Animal animal : model.getAllAnimals()) {
            if ("Available".equals(animal.getStatus())) {
                tableModel.addRow(new Object[] {animal.getId(), animal.getName(), animal.getSpecies(),
                        animal.getBreed(), animal.getAge(), animal.getHealthStatus()});
            }
        }
    }

    private void createRequest() {
        ArrayList<Animal> available = new ArrayList<>();
        for (Animal animal : model.getAllAnimals()) {
            if ("Available".equals(animal.getStatus())) available.add(animal);
        }
        if (available.isEmpty()) {
            showMessage("No animals are available.");
            return;
        }
        String[] choices = new String[available.size()];
        for (int i = 0; i < available.size(); i++) choices[i] = available.get(i).getName();
        String selected = (String) JOptionPane.showInputDialog(this, "Animal", "New adoption request",
                JOptionPane.PLAIN_MESSAGE, null, choices, choices[0]);
        if (selected == null) return;
        Animal animal = available.get(java.util.Arrays.asList(choices).indexOf(selected));
        ArrayList<String> addOns = new ArrayList<>();
        for (String addOn : PackageFactory.ADD_ONS) {
            if (JOptionPane.showConfirmDialog(this, "Add " + addOn + "?", "Adoption package",
                    JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) addOns.add(addOn);
        }
        model.addAdoption(new AdoptionRequest(model.nextAdoptionId(), adopter, animal, addOns));
        database.save(model);
        showRequests();
        showMessage("Adoption request submitted.");
    }

    private void showRequests() {
        tableModel.setColumnIdentifiers(new Object[] {"ID", "Animal", "Status", "Package"});
        tableModel.setRowCount(0);
        for (AdoptionRequest request : model.getAllAdoptions()) {
            if (request.getAdopter() == adopter) {
                tableModel.addRow(new Object[] {request.getId(), request.getAnimal().getName(),
                        request.getStatus(), request.getAdoptionPackage().getDescription()});
            }
        }
    }

    private void showProfile() {
        showMessage("Name: " + adopter.getName() + "\nEmail: " + adopter.getEmail()
                + "\nPhone: " + adopter.getPhone() + "\nHome type: " + adopter.getHomeType());
    }

    private void showMessage(String message) {
        JOptionPane.showMessageDialog(this, message, "Paws & Whiskers", JOptionPane.INFORMATION_MESSAGE);
    }
}
