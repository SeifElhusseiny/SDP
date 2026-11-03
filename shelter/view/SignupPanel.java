package shelter.view;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;

import shelter.model.User;

public class SignupPanel extends JPanel {
    public SignupPanel(SignupHandler signupHandler, Runnable backAction) {
        setLayout(new GridBagLayout());
        setBorder(BorderFactory.createEmptyBorder(30, 150, 30, 150));

        JTextField nameField = new JTextField(22);
        JTextField emailField = new JTextField(22);
        JTextField phoneField = new JTextField(22);
        JPasswordField passwordField = new JPasswordField(22);
        JPasswordField confirmationField = new JPasswordField(22);
        JComboBox<String> roleBox = new JComboBox<>(new String[] {"Adopter", "Donor"});
        JLabel errorLabel = new JLabel(" ");
        errorLabel.setForeground(java.awt.Color.RED);
        GridBagConstraints constraints = new GridBagConstraints();
        constraints.insets = new Insets(6, 8, 6, 8);
        constraints.fill = GridBagConstraints.HORIZONTAL;

        addField("Name:", nameField, constraints, 0);
        addField("Email:", emailField, constraints, 1);
        addField("Phone:", phoneField, constraints, 2);
        addField("Password:", passwordField, constraints, 3);
        addField("Confirm password:", confirmationField, constraints, 4);
        addField("Account type:", roleBox, constraints, 5);
        constraints.gridx = 0;
        constraints.gridy = 6;
        constraints.gridwidth = 2;
        add(errorLabel, constraints);

        JButton signupButton = new JButton("Create account");
        signupButton.addActionListener(event -> {
            try {
                if (!new String(passwordField.getPassword()).equals(new String(confirmationField.getPassword()))) {
                    throw new IllegalArgumentException("Passwords do not match.");
                }
                User user = signupHandler.signup((String) roleBox.getSelectedItem(), nameField.getText(),
                        emailField.getText(), phoneField.getText(), passwordField.getPassword());
                errorLabel.setText("Account created. You can now log in.");
                JOptionPane.showMessageDialog(this, "Account created for " + user.getName() + ".");
                backAction.run();
            } catch (IllegalArgumentException exception) {
                errorLabel.setText(exception.getMessage());
            }
        });
        constraints.gridy = 7;
        add(signupButton, constraints);

        JButton backButton = new JButton("Back");
        backButton.addActionListener(event -> backAction.run());
        constraints.gridy = 8;
        add(backButton, constraints);
    }

    private void addField(String label, java.awt.Component field, GridBagConstraints constraints, int row) {
        constraints.gridx = 0;
        constraints.gridy = row;
        constraints.gridwidth = 1;
        add(new JLabel(label), constraints);
        constraints.gridx = 1;
        add(field, constraints);
    }

    public interface SignupHandler {
        User signup(String role, String name, String email, String phone, char[] password);
    }
}
