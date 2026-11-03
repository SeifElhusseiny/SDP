package shelter.view;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Window;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;

import shelter.database.JsonDatabase;
import shelter.model.Animal;
import shelter.model.ShelterModel;
import shelter.model.User;
import shelter.patterns.strategy.SocialMediaNotification;

public final class CommunicationPanel {
    private CommunicationPanel() {
    }

    public static void show(Window owner, ShelterModel model, JsonDatabase database) {
        JDialog dialog = new JDialog(owner, "Communication", java.awt.Dialog.ModalityType.APPLICATION_MODAL);
        dialog.setLayout(new BorderLayout(8, 8));
        dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);

        JTabbedPane tabs = new JTabbedPane();
        tabs.addTab("One user", createDirectMessageTab(dialog, model, database));
        tabs.addTab("Channel subscribers", createChannelMessageTab(dialog, model, database));
        tabs.addTab("Social media post", createSocialPostTab(dialog, model, database));
        dialog.add(tabs, BorderLayout.CENTER);

        JPanel footer = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton close = new JButton("Close");
        close.addActionListener(event -> dialog.dispose());
        footer.add(close);
        dialog.add(footer, BorderLayout.SOUTH);
        ((JPanel) dialog.getContentPane()).setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        dialog.setSize(680, 440);
        dialog.setLocationRelativeTo(owner);
        dialog.setVisible(true);
    }

    private static JPanel createDirectMessageTab(JDialog dialog, ShelterModel model, JsonDatabase database) {
        JPanel panel = new JPanel(new BorderLayout(8, 8));
        JComboBox<User> recipients = new JComboBox<>(model.getAllUsers().toArray(new User[0]));
        recipients.setRenderer((list, user, index, isSelected, cellHasFocus) -> {
            JLabel label = new JLabel(user == null ? "" : "#" + user.getId() + " | " + user.getRole()
                    + " | " + user.getName() + " | " + user.getEmail());
            if (isSelected) {
                label.setOpaque(true);
                label.setBackground(list.getSelectionBackground());
                label.setForeground(list.getSelectionForeground());
            }
            return label;
        });
        JComboBox<String> channels = new JComboBox<>(new String[] {"Email", "Phone (SMS)", "Social media"});
        JTextArea message = new JTextArea(8, 40);
        message.setLineWrap(true);
        message.setWrapStyleWord(true);
        JPanel fields = new JPanel(new BorderLayout(6, 6));
        JPanel choices = new JPanel(new BorderLayout(6, 6));
        choices.add(new JLabel("Recipient:"), BorderLayout.WEST);
        choices.add(recipients, BorderLayout.CENTER);
        JPanel channelRow = new JPanel(new BorderLayout(6, 6));
        channelRow.add(new JLabel("Send by:"), BorderLayout.WEST);
        channelRow.add(channels, BorderLayout.CENTER);
        JPanel choiceRows = new JPanel(new BorderLayout(0, 6));
        choiceRows.add(choices, BorderLayout.NORTH);
        choiceRows.add(channelRow, BorderLayout.SOUTH);
        fields.add(choiceRows, BorderLayout.NORTH);
        fields.add(new JLabel("Message:"), BorderLayout.CENTER);
        panel.add(fields, BorderLayout.NORTH);
        panel.add(new JScrollPane(message), BorderLayout.CENTER);

        JButton send = new JButton("Send");
        send.addActionListener(event -> {
            User recipient = (User) recipients.getSelectedItem();
            if (recipient == null) {
                showError(dialog, "There are no users to message.");
                return;
            }
            String text = message.getText().trim();
            if (text.isEmpty()) {
                showError(dialog, "Message cannot be empty.");
                return;
            }
            String channel = "Phone (SMS)".equals(channels.getSelectedItem())
                    ? "SMS" : (String) channels.getSelectedItem();
            recipient.notifyOnChannel(channel, text);
            database.save(model);
            JOptionPane.showMessageDialog(dialog, "Message sent to " + recipient.getName()
                    + " by " + channels.getSelectedItem() + ".", "Message sent",
                    JOptionPane.INFORMATION_MESSAGE);
            message.setText("");
        });
        JPanel actions = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        actions.add(send);
        panel.add(actions, BorderLayout.SOUTH);
        return panel;
    }

    private static JPanel createChannelMessageTab(JDialog dialog, ShelterModel model, JsonDatabase database) {
        JPanel panel = new JPanel(new BorderLayout(8, 8));
        JComboBox<String> channels = new JComboBox<>(new String[] {"Email", "Phone (SMS)", "Social media"});
        JTextArea message = new JTextArea(8, 40);
        message.setLineWrap(true);
        message.setWrapStyleWord(true);
        JPanel top = new JPanel(new BorderLayout(6, 6));
        top.add(new JLabel("Send to subscribers of:"), BorderLayout.WEST);
        top.add(channels, BorderLayout.CENTER);
        panel.add(top, BorderLayout.NORTH);
        panel.add(new JScrollPane(message), BorderLayout.CENTER);

        JButton send = new JButton("Send to subscribers");
        send.addActionListener(event -> {
            String selectedLabel = (String) channels.getSelectedItem();
            String channel = channelName(selectedLabel);
            List<User> recipients = getChannelSubscribers(model, channel);
            if (recipients.isEmpty()) {
                showError(dialog, "There are no users subscribed to " + selectedLabel + ".");
                return;
            }
            String text = message.getText().trim();
            if (text.isEmpty()) {
                showError(dialog, "Message cannot be empty.");
                return;
            }
            recipients.forEach(recipient -> recipient.notifyOnChannel(channel, text));
            database.save(model);
            JOptionPane.showMessageDialog(dialog, "Message sent to " + recipients.size()
                    + " " + selectedLabel + " subscriber(s).", "Messages sent",
                    JOptionPane.INFORMATION_MESSAGE);
            message.setText("");
        });
        JPanel actions = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        actions.add(send);
        panel.add(actions, BorderLayout.SOUTH);
        return panel;
    }

    private static JPanel createSocialPostTab(JDialog dialog, ShelterModel model, JsonDatabase database) {
        JPanel panel = new JPanel(new BorderLayout(8, 8));
        JTextArea customMessage = new JTextArea(7, 40);
        customMessage.setLineWrap(true);
        customMessage.setWrapStyleWord(true);
        panel.add(new JLabel("Custom post (published and sent to all social-media subscribers):"),
                BorderLayout.NORTH);
        panel.add(new JScrollPane(customMessage), BorderLayout.CENTER);

        JButton publishCustomPost = new JButton("Publish custom post");
        publishCustomPost.addActionListener(event -> {
            String text = customMessage.getText().trim();
            if (text.isEmpty()) {
                showError(dialog, "Post cannot be empty.");
                return;
            }
            int recipients = publishSocialPost(model, text);
            database.save(model);
            JOptionPane.showMessageDialog(dialog,
                    "Custom post published and sent to " + recipients + " social-media subscriber(s).",
                    "Social media", JOptionPane.INFORMATION_MESSAGE);
            customMessage.setText("");
        });

        JButton postAvailableAnimals = new JButton("Post available animals");
        postAvailableAnimals.addActionListener(event -> {
            int posted = 0;
            int recipients = 0;
            for (Animal animal : model.getAllAnimals()) {
                if ("Available".equals(animal.getStatus())) {
                    String post = "Meet " + animal.getName() + ", a " + animal.getAge() + "-year-old "
                            + animal.getBreed() + " (" + animal.getSpecies()
                            + ") looking for a loving home! #AdoptDontShop";
                    recipients += publishSocialPost(model, post);
                    posted++;
                }
            }
            if (posted > 0) {
                database.save(model);
            }
            JOptionPane.showMessageDialog(dialog, posted == 0
                    ? "No animals are available right now."
                    : posted + " post(s) published and sent to social-media subscribers ("
                            + recipients + " message deliveries).",
                    "Social media", JOptionPane.INFORMATION_MESSAGE);
        });
        JPanel actions = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        actions.add(postAvailableAnimals);
        actions.add(publishCustomPost);
        panel.add(actions, BorderLayout.SOUTH);
        return panel;
    }

    private static int publishSocialPost(ShelterModel model, String post) {
        new SocialMediaNotification().postToPage(post);
        List<User> recipients = getChannelSubscribers(model, "Social media");
        recipients.forEach(recipient -> recipient.notifyOnChannel("Social media", post));
        return recipients.size();
    }

    private static List<User> getChannelSubscribers(ShelterModel model, String channel) {
        List<User> subscribers = new java.util.ArrayList<>();
        for (User user : model.getAllUsers()) {
            if (user.getPreferredChannels().contains(channel)) {
                subscribers.add(user);
            }
        }
        return subscribers;
    }

    private static String channelName(String label) {
        return "Phone (SMS)".equals(label) ? "SMS" : label;
    }

    private static void showError(JDialog dialog, String message) {
        JOptionPane.showMessageDialog(dialog, message, "Unable to send message",
                JOptionPane.ERROR_MESSAGE);
    }
}
