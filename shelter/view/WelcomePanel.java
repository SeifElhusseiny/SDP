package shelter.view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

public class WelcomePanel extends JPanel {
    public WelcomePanel(Runnable loginAction, Runnable signupAction, Runnable exitAction) {
        setLayout(new BorderLayout());
        setBackground(new Color(245, 248, 247));
        setBorder(BorderFactory.createEmptyBorder(60, 120, 60, 120));

        JLabel title = new JLabel("Paws & Whiskers", SwingConstants.CENTER);
        title.setFont(new Font("SansSerif", Font.BOLD, 42));
        title.setForeground(new Color(45, 80, 72));
        add(title, BorderLayout.NORTH);

        JPanel center = new JPanel(new BorderLayout(0, 20));
        center.setOpaque(false);

        JLabel description = new JLabel(
                "<html><div style='text-align: center;'>Animal shelter management and adoption system<br>"
                        + "Find a way to help animals find loving homes.</div></html>",
                SwingConstants.CENTER);
        description.setFont(new Font("SansSerif", Font.PLAIN, 18));
        center.add(description, BorderLayout.CENTER);

        JPanel actions = new JPanel(new GridLayout(1, 3, 14, 0));
        actions.setOpaque(false);
        actions.setPreferredSize(new Dimension(420, 48));
        actions.add(createButton("Login", loginAction));
        actions.add(createButton("Sign Up", signupAction));
        actions.add(createButton("Exit", exitAction));
        center.add(actions, BorderLayout.SOUTH);

        add(center, BorderLayout.CENTER);
    }

    private JButton createButton(String text, Runnable action) {
        JButton button = new JButton(text);
        button.setFont(new Font("SansSerif", Font.BOLD, 15));
        button.addActionListener(event -> action.run());
        return button;
    }
}
