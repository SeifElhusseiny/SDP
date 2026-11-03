package shelter.view;

import java.awt.Component;
import java.awt.GridLayout;
import java.util.Arrays;

import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

import shelter.database.JsonDatabase;
import shelter.model.Adopter;
import shelter.model.Donor;
import shelter.model.ShelterModel;
import shelter.model.User;
import shelter.model.Validator;

final class ProfileSettingsDialog {
    private static final String[] HOME_TYPES = {"Apartment", "House with garden", "Farm"};

    private ProfileSettingsDialog() {
    }

    static void show(Component parent, ShelterModel model, JsonDatabase database, User user) {
        String details = "Name: " + user.getName() + "\nEmail: " + user.getEmail()
                + "\nPhone: " + user.getPhone();
        if (user instanceof Donor) {
            details += "\nTotal donated: " + ((Donor) user).getTotalDonated() + " EGP";
        } else if (user instanceof Adopter) {
            details += "\nHome type: " + ((Adopter) user).getHomeType();
        }
        if (JOptionPane.showConfirmDialog(parent, details + "\n\nEdit profile settings?",
                "My profile", JOptionPane.YES_NO_OPTION, JOptionPane.INFORMATION_MESSAGE)
                != JOptionPane.YES_OPTION) return;

        JTextField name = new JTextField(user.getName(), 24);
        JTextField email = new JTextField(user.getEmail(), 24);
        JTextField phone = new JTextField(user.getPhone(), 24);
        NotificationChannelSelector channels = new NotificationChannelSelector(user.getPreferredChannels());
        JPanel fields = new JPanel(new GridLayout(0, 2, 8, 6));
        fields.add(new JLabel("Name:"));
        fields.add(name);
        fields.add(new JLabel("Email:"));
        fields.add(email);
        fields.add(new JLabel("Phone:"));
        fields.add(phone);

        JComboBox<String> homeType = null;
        if (user instanceof Adopter) {
            homeType = new JComboBox<>(HOME_TYPES);
            String current = ((Adopter) user).getHomeType();
            if (!Arrays.asList(HOME_TYPES).contains(current)) homeType.addItem(current);
            homeType.setSelectedItem(current);
            fields.add(new JLabel("Home type:"));
            fields.add(homeType);
        }
        fields.add(new JLabel("Notification channels:"));
        fields.add(channels);
        if (JOptionPane.showConfirmDialog(parent, fields, "Edit profile",
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE) != JOptionPane.OK_OPTION) return;

        String cleanName = name.getText().trim();
        String cleanEmail = email.getText().trim();
        String cleanPhone = phone.getText().trim();
        if (cleanName.isEmpty()) {
            showError(parent, "Name cannot be empty.");
            return;
        }
        if (!Validator.isValidEmail(cleanEmail)) {
            showError(parent, Validator.EMAIL_RULE);
            return;
        }
        for (User existing : model.getAllUsers()) {
            if (existing.getId() != user.getId() && existing.getEmail().equalsIgnoreCase(cleanEmail)) {
                showError(parent, "An account with this email already exists.");
                return;
            }
        }
        if (!Validator.isValidPhone(cleanPhone)) {
            showError(parent, Validator.PHONE_RULE);
            return;
        }
        try {
            user.setPreferredChannels(channels.getSelectedChannels());
        } catch (IllegalArgumentException exception) {
            showError(parent, exception.getMessage());
            return;
        }
        user.setName(cleanName);
        user.setEmail(cleanEmail);
        user.setPhone(cleanPhone);
        if (user instanceof Adopter) ((Adopter) user).setHomeType((String) homeType.getSelectedItem());
        database.save(model);
        JOptionPane.showMessageDialog(parent, "Profile settings saved.",
                "My profile", JOptionPane.INFORMATION_MESSAGE);
    }

    private static void showError(Component parent, String message) {
        JOptionPane.showMessageDialog(parent, message, "Profile update failed", JOptionPane.ERROR_MESSAGE);
    }
}
