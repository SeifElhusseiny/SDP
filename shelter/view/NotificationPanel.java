package shelter.view;

import java.awt.BorderLayout;
import java.awt.Window;

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
        dialog.add(new JLabel("Notification history"), BorderLayout.NORTH);

        JList<String> notifications = new JList<>(user.getNotifications().toArray(new String[0]));
        notifications.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        dialog.add(new JScrollPane(notifications), BorderLayout.CENTER);

        JButton close = new JButton("Close");
        close.addActionListener(event -> dialog.dispose());
        JPanel footer = new JPanel();
        footer.add(close);
        dialog.add(footer, BorderLayout.SOUTH);
        ((JPanel) dialog.getContentPane()).setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));
        dialog.setSize(520, 360);
        dialog.setLocationRelativeTo(owner);
        dialog.setVisible(true);
    }
}
