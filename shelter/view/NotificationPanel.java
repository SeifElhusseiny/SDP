package shelter.view;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.Window;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;

import shelter.model.User;

public final class NotificationPanel {
    private NotificationPanel() {
    }

    public static void show(Window owner, User user) {
        JDialog dialog = new JDialog(owner, "Notifications - " + user.getName(),
                java.awt.Dialog.ModalityType.APPLICATION_MODAL);
        dialog.setLayout(new BorderLayout(10, 10));
        dialog.add(new JLabel("Notification history by channel"), BorderLayout.NORTH);

        JPanel channels = new JPanel(new GridLayout(1, 3, 8, 0));
        channels.add(createChannelPanel("Email", user.getEmailNotifications()));
        channels.add(createChannelPanel("Phone (SMS)", user.getPhoneNotifications()));
        channels.add(createChannelPanel("Social media", user.getSocialMediaNotifications()));
        dialog.add(channels, BorderLayout.CENTER);

        JButton close = new JButton("Close");
        close.addActionListener(event -> dialog.dispose());
        JPanel footer = new JPanel();
        footer.add(close);
        dialog.add(footer, BorderLayout.SOUTH);
        ((JPanel) dialog.getContentPane()).setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));
        dialog.setSize(900, 420);
        dialog.setLocationRelativeTo(owner);
        dialog.setVisible(true);
    }

    private static JPanel createChannelPanel(String title, List<String> messages) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createTitledBorder(title));
        JList<String> notifications = new JList<>(messages.toArray(new String[0]));
        notifications.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        panel.add(new JScrollPane(notifications), BorderLayout.CENTER);
        return panel;
    }
}
