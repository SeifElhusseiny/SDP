package shelter.view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.GridLayout;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

import shelter.model.AdoptionRequest;
import shelter.model.Animal;
import shelter.model.ShelterModel;

public class DashboardPanel extends JPanel {
    private final ShelterModel model;
    private final JLabel animalsValue = createValueLabel();
    private final JLabel availableAnimalsValue = createValueLabel();
    private final JLabel usersValue = createValueLabel();
    private final JLabel pendingAdoptionsValue = createValueLabel();
    private final JLabel donationsValue = createValueLabel();
    private final JLabel eventsValue = createValueLabel();

    public DashboardPanel(ShelterModel model) {
        this.model = model;
        setLayout(new BorderLayout(0, 24));
        setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));

        JLabel title = new JLabel("Dashboard");
        title.setFont(new Font("SansSerif", Font.BOLD, 28));
        add(title, BorderLayout.NORTH);

        JPanel cards = new JPanel(new GridLayout(2, 3, 16, 16));
        cards.add(createCard("Total animals", animalsValue));
        cards.add(createCard("Available animals", availableAnimalsValue));
        cards.add(createCard("Users", usersValue));
        cards.add(createCard("Pending adoptions", pendingAdoptionsValue));
        cards.add(createCard("Donations", donationsValue));
        cards.add(createCard("Events", eventsValue));
        add(cards, BorderLayout.CENTER);

        refresh();
    }

    public void refresh() {
        List<Animal> animals = model.getAllAnimals();
        int availableAnimals = 0;
        for (Animal animal : animals) {
            if ("Available".equalsIgnoreCase(animal.getStatus())) {
                availableAnimals++;
            }
        }

        int pendingAdoptions = 0;
        for (AdoptionRequest adoption : model.getAllAdoptions()) {
            if ("Pending".equalsIgnoreCase(adoption.getStatus())) {
                pendingAdoptions++;
            }
        }

        animalsValue.setText(String.valueOf(animals.size()));
        availableAnimalsValue.setText(String.valueOf(availableAnimals));
        usersValue.setText(String.valueOf(model.getAllUsers().size()));
        pendingAdoptionsValue.setText(String.valueOf(pendingAdoptions));
        donationsValue.setText(String.valueOf(model.getAllDonations().size()));
        eventsValue.setText(String.valueOf(model.getAllEvents().size()));
    }

    private JPanel createCard(String title, JLabel valueLabel) {
        JPanel card = new JPanel(new BorderLayout(0, 8));
        card.setBackground(new Color(245, 248, 247));
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(205, 218, 214)),
                BorderFactory.createEmptyBorder(18, 18, 18, 18)));

        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("SansSerif", Font.PLAIN, 16));
        card.add(titleLabel, BorderLayout.NORTH);
        card.add(valueLabel, BorderLayout.CENTER);
        return card;
    }

    private JLabel createValueLabel() {
        JLabel label = new JLabel("0", SwingConstants.CENTER);
        label.setFont(new Font("SansSerif", Font.BOLD, 34));
        label.setForeground(new Color(45, 80, 72));
        return label;
    }
}
