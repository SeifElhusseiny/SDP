package shelter.view;

import java.awt.BorderLayout;
import java.awt.GridLayout;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

import shelter.database.JsonDatabase;
import shelter.model.Donation;
import shelter.model.Donor;
import shelter.model.ShelterModel;
import shelter.patterns.factory.StrategyFactory;
import shelter.patterns.strategy.PaymentStrategy;

public class DonorDashboardPanel extends JPanel {
    private final ShelterModel model;
    private final JsonDatabase database;
    private final Donor donor;
    private final DefaultTableModel tableModel = new DefaultTableModel();
    private final JTable table = new JTable(tableModel);

    public DonorDashboardPanel(ShelterModel model, JsonDatabase database, Donor donor, Runnable logoutAction) {
        this.model = model;
        this.database = database;
        this.donor = donor;
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(18, 18, 18, 18));
        JPanel actions = new JPanel(new GridLayout(2, 3, 8, 8));
        addButton(actions, "Make a donation", this::makeDonation);
        addButton(actions, "Donation history", this::showHistory);
        addButton(actions, "Receipt", this::showReceipt);
        addButton(actions, "My profile", this::showProfile);
        addButton(actions, "Notifications", () -> NotificationPanel.show(
                javax.swing.SwingUtilities.getWindowAncestor(this), donor));
        addButton(actions, "Logout", logoutAction);
        add(actions, BorderLayout.NORTH);
        add(new JScrollPane(table), BorderLayout.CENTER);
        showHistory();
    }

    private void addButton(JPanel panel, String text, Runnable action) {
        JButton button = new JButton(text);
        button.addActionListener(event -> action.run());
        panel.add(button);
    }

    private void makeDonation() {
        try {
            String type = (String) JOptionPane.showInputDialog(this, "Donation type", "Donation",
                    JOptionPane.PLAIN_MESSAGE, null, model.getDonationTypes().getAll().toArray(), "Money");
            if (type == null) return;
            double amount = Double.parseDouble(required("Amount or estimated value (EGP)"));
            if (amount <= 0) throw new IllegalArgumentException("Amount must be greater than zero.");
            String paymentMethod = "In-kind";
            if ("Money".equals(type)) {
                paymentMethod = choosePayment(amount);
            }
            Donation donation = new Donation(model.nextDonationId(), donor, type, amount, paymentMethod);
            model.addDonation(donation);
            donor.addToTotal(amount);
            donation.attach(donor);
            for (shelter.model.User admin : model.getUsersByRole("Admin")) {
                donation.attach(admin);
            }
            donation.notifyObservers("Donation #" + donation.getId() + " received. Thank you!");
            database.save(model);
            showHistory();
            JOptionPane.showMessageDialog(this,
                    "Notification sent to you and the shelter administrators:\n\n"
                            + "Donation #" + donation.getId() + " received. Thank you!",
                    "Notification", JOptionPane.INFORMATION_MESSAGE);
            JOptionPane.showMessageDialog(this, donation.generateReceipt(), "Donation receipt",
                    JOptionPane.INFORMATION_MESSAGE);
        } catch (RuntimeException exception) {
            JOptionPane.showMessageDialog(this, exception.getMessage(), "Donation failed",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private String choosePayment(double amount) {
        String method = (String) JOptionPane.showInputDialog(this, "Payment method", "Donation",
                JOptionPane.PLAIN_MESSAGE, null, StrategyFactory.PAYMENT_METHODS, "Cash");
        if (method == null) throw new IllegalArgumentException("Payment method is required.");
        PaymentStrategy payment = StrategyFactory.createPayment(method);
        String[] fields = payment.getRequiredDetails();
        String[] details = new String[fields.length];
        for (int i = 0; i < fields.length; i++) {
            details[i] = required(fields[i]);
            if (!payment.isValidDetail(i, details[i])) {
                throw new IllegalArgumentException("Invalid " + fields[i] + ".");
            }
        }
        JOptionPane.showMessageDialog(this, payment.pay(donor.getName(), amount, details),
                "Payment", JOptionPane.INFORMATION_MESSAGE);
        return payment.getReference(details);
    }

    private void showHistory() {
        tableModel.setColumnIdentifiers(new Object[] {"ID", "Date", "Type", "Amount", "Payment"});
        tableModel.setRowCount(0);
        for (Donation donation : model.getAllDonations()) {
            if (donation.getDonor() == donor) {
                tableModel.addRow(new Object[] {donation.getId(), donation.getDate(), donation.getType(),
                        donation.getAmount(), donation.getPaymentMethod()});
            }
        }
    }

    private void showReceipt() {
        try {
            int id = Integer.parseInt(required("Donation ID"));
            for (Donation donation : model.getAllDonations()) {
                if (donation.getId() == id && donation.getDonor() == donor) {
                    JOptionPane.showMessageDialog(this, donation.generateReceipt(), "Donation receipt",
                            JOptionPane.INFORMATION_MESSAGE);
                    return;
                }
            }
            throw new IllegalArgumentException("That donation does not belong to this donor.");
        } catch (RuntimeException exception) {
            JOptionPane.showMessageDialog(this, exception.getMessage(), "Receipt", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void showProfile() {
        ProfileSettingsDialog.show(this, model, database, donor);
    }

    private String required(String label) {
        String value = JOptionPane.showInputDialog(this, label);
        if (value == null || value.trim().isEmpty()) throw new IllegalArgumentException(label + " is required.");
        return value.trim();
    }
}
