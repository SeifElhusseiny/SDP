package shelter.view;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;

import shelter.model.User;

public class LoginPanel extends JPanel {
    public LoginPanel(LoginHandler loginHandler, Runnable backAction) {
        setLayout(new GridBagLayout());
        setBorder(BorderFactory.createEmptyBorder(50, 180, 50, 180));

        JTextField emailField = new JTextField(22);
        JPasswordField passwordField = new JPasswordField(22);
        JLabel errorLabel = new JLabel(" ");
        errorLabel.setForeground(java.awt.Color.RED);

        GridBagConstraints constraints = new GridBagConstraints();
        constraints.insets = new Insets(8, 8, 8, 8);
        constraints.fill = GridBagConstraints.HORIZONTAL;
        addField("Email:", emailField, constraints, 0);
        addField("Password:", passwordField, constraints, 1);
        constraints.gridx = 0;
        constraints.gridy = 2;
        constraints.gridwidth = 2;
        add(errorLabel, constraints);

        JButton loginButton = new JButton("Login");
        loginButton.addActionListener(event -> {
            try {
                User user = loginHandler.login(emailField.getText(), passwordField.getPassword());
                errorLabel.setText(" ");
                loginHandler.success(user);
            } catch (IllegalArgumentException exception) {
                errorLabel.setText(exception.getMessage());
            }
        });
        constraints.gridy = 3;
        add(loginButton, constraints);

        JButton backButton = new JButton("Back");
        backButton.addActionListener(event -> backAction.run());
        constraints.gridy = 4;
        add(backButton, constraints);
    }

    private void addField(String label, JTextField field, GridBagConstraints constraints, int row) {
        constraints.gridx = 0;
        constraints.gridy = row;
        constraints.gridwidth = 1;
        add(new JLabel(label), constraints);
        constraints.gridx = 1;
        add(field, constraints);
    }

    public interface LoginHandler {
        User login(String email, char[] password);
        void success(User user);
    }
}
