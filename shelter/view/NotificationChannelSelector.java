package shelter.view;

import java.awt.GridLayout;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JCheckBox;
import javax.swing.JPanel;

import shelter.patterns.factory.strategy.AbstractNotificationFactory;

public class NotificationChannelSelector extends JPanel {
    private final List<JCheckBox> channelBoxes = new ArrayList<>();

    public NotificationChannelSelector(List<AbstractNotificationFactory> notificationFactories) {
        this(notificationFactories, Collections.singletonList("Email"));
    }

    public NotificationChannelSelector(List<AbstractNotificationFactory> notificationFactories,
            List<String> selectedChannels) {
        setLayout(new GridLayout(1, notificationFactories.size(), 8, 0));
        setBorder(BorderFactory.createTitledBorder("Notification channels"));
        for (AbstractNotificationFactory factory : notificationFactories) {
            String channel = factory.getChannel();
            String label = "SMS".equals(channel) ? "Phone (SMS)" : channel;
            JCheckBox channelBox = new JCheckBox(label, selectedChannels.contains(channel));
            channelBox.putClientProperty("channel", channel);
            channelBoxes.add(channelBox);
            add(channelBox);
        }
    }

    public List<String> getSelectedChannels() {
        List<String> selectedChannels = new ArrayList<>();
        for (JCheckBox channelBox : channelBoxes) {
            if (channelBox.isSelected()) {
                selectedChannels.add((String) channelBox.getClientProperty("channel"));
            }
        }
        if (selectedChannels.isEmpty()) {
            throw new IllegalArgumentException("Select at least one notification channel.");
        }
        return selectedChannels;
    }
}
