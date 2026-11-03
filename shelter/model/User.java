package shelter.model;

import java.util.ArrayList;
import java.util.List;

import shelter.patterns.factory.strategy.AbstractNotificationFactory;
import shelter.patterns.observer.Observer;
import shelter.patterns.strategy.NotificationService;

// Parent of every person in the system. Every user is an OBSERVER:
// when an event or donation notifies them, the message is sent through
// EACH of their preferred channels (STRATEGY).
public abstract class User implements Observer {
    private final int id;
    private String name;
    private String email;
    private String phone;
    private List<String> preferredChannels;
    private final List<AbstractNotificationFactory> notificationFactories;
    private String passwordHash = "";
    private final List<String> emailNotifications = new ArrayList<>();
    private final List<String> phoneNotifications = new ArrayList<>();
    private final List<String> socialMediaNotifications = new ArrayList<>();

    public User(int id, String name, String email, String phone, List<String> preferredChannels,
            List<AbstractNotificationFactory> notificationFactories) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.phone = phone;
        this.notificationFactories = new ArrayList<>(notificationFactories);
        this.preferredChannels = atLeastOneChannel(preferredChannels);
    }

    // A user always has at least one channel (Email by default).
    private static List<String> atLeastOneChannel(List<String> channels) {
        List<String> result = new ArrayList<>(channels);
        if (result.isEmpty()) {
            result.add("Email");
        }
        return result;
    }

    public abstract String getRole();

    // Role-specific details, shown after the common fields.
    public String getExtraDetails() {
        return "";
    }

    @Override
    public void update(String message) {
        for (String channel : preferredChannels) {
            sendNotification(channel, message);
        }
    }

    public void notifyOnChannel(String channel, String message) {
        sendNotification(channel, message);
    }

    private void sendNotification(String channel, String message) {
        AbstractNotificationFactory factory = findNotificationFactory(channel);
        addNotification(channel, message);
        NotificationService service = new NotificationService(factory.createNotification());
        service.notify(this, message);
    }

    private AbstractNotificationFactory findNotificationFactory(String channel) {
        for (AbstractNotificationFactory factory : notificationFactories) {
            if (factory.getChannel().equals(channel)) {
                return factory;
            }
        }
        throw new IllegalArgumentException("Unknown notification channel: " + channel);
    }

    private void addNotification(String channel, String message) {
        switch (channel) {
            case "SMS":
                phoneNotifications.add(message);
                break;
            case "Social media":
                socialMediaNotifications.add(message);
                break;
            default:
                emailNotifications.add(message);
                break;
        }
    }

    public int getId() { return id; }
    public String getName() { return name; }
    public String getEmail() { return email; }
    public String getPhone() { return phone; }
    public List<String> getPreferredChannels() { return new ArrayList<>(preferredChannels); }
    public String getPasswordHash() { return passwordHash; }
    public List<String> getEmailNotifications() { return new ArrayList<>(emailNotifications); }
    public List<String> getPhoneNotifications() { return new ArrayList<>(phoneNotifications); }
    public List<String> getSocialMediaNotifications() { return new ArrayList<>(socialMediaNotifications); }

    public List<String> getNotifications() {
        List<String> allNotifications = new ArrayList<>(emailNotifications);
        allNotifications.addAll(phoneNotifications);
        allNotifications.addAll(socialMediaNotifications);
        return allNotifications;
    }

    public void setName(String name) { this.name = name; }
    public void setEmail(String email) { this.email = email; }
    public void setPhone(String phone) { this.phone = phone; }
    public void setPasswordHash(String passwordHash) { this.passwordHash = passwordHash == null ? "" : passwordHash; }

    public void setEmailNotifications(List<String> messages) {
        replaceNotifications(emailNotifications, messages);
    }

    public void setPhoneNotifications(List<String> messages) {
        replaceNotifications(phoneNotifications, messages);
    }

    public void setSocialMediaNotifications(List<String> messages) {
        replaceNotifications(socialMediaNotifications, messages);
    }

    public void setNotifications(List<String> messages) {
        setEmailNotifications(null);
        setPhoneNotifications(null);
        setSocialMediaNotifications(null);
        for (String channel : preferredChannels) {
            addNotifications(channel, messages);
        }
    }

    private void addNotifications(String channel, List<String> messages) {
        if (messages == null) {
            return;
        }
        switch (channel) {
            case "SMS":
                phoneNotifications.addAll(messages);
                break;
            case "Social media":
                socialMediaNotifications.addAll(messages);
                break;
            default:
                emailNotifications.addAll(messages);
                break;
        }
    }

    private void replaceNotifications(List<String> destination, List<String> messages) {
        destination.clear();
        if (messages != null) {
            destination.addAll(messages);
        }
    }

    public void setPreferredChannels(List<String> preferredChannels) {
        for (String channel : preferredChannels) {
            findNotificationFactory(channel);
        }
        this.preferredChannels = atLeastOneChannel(preferredChannels);
    }

    @Override
    public String toString() {
        return "#" + id + " | " + getRole() + " | " + name + " | " + email + " | " + phone
                + " | Notify by: " + String.join(", ", preferredChannels) + getExtraDetails();
    }
}
