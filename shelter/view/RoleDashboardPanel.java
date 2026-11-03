package shelter.view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.GridLayout;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

import shelter.model.User;

public class RoleDashboardPanel extends JPanel {
    public RoleDashboardPanel(User user, String[] actions, Runnable logoutAction, Runnable actionHandler) {
        setLayout(new BorderLayout(0, 24));
        setBorder(BorderFactory.createEmptyBorder(35, 55, 35, 55));

        JLabel title = new JLabel("Welcome, " + user.getName(), SwingConstants.CENTER);
        title.setFont(new Font("SansSerif", Font.BOLD, 30));
        title.setForeground(new Color(45, 80, 72));
        add(title, BorderLayout.NORTH);

        JPanel center = new JPanel(new BorderLayout(0, 18));
        JLabel role = new JLabel("Logged in as: " + user.getRole(), SwingConstants.CENTER);
        role.setFont(new Font("SansSerif", Font.PLAIN, 18));
        center.add(role, BorderLayout.NORTH);

        JPanel actionGrid = new JPanel(new GridLayout(0, 2, 14, 14));
        for (String action : actions) {
            JButton button = new JButton(action);
            button.addActionListener(event -> actionHandler.run());
            actionGrid.add(button);
        }
        center.add(actionGrid, BorderLayout.CENTER);
        add(center, BorderLayout.CENTER);

        JButton logout = new JButton("Logout");
        logout.addActionListener(event -> logoutAction.run());
        JPanel footer = new JPanel();
        footer.add(logout);
        add(footer, BorderLayout.SOUTH);
    }
}
