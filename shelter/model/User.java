package shelter.model;

import java.util.ArrayList;
import java.util.List;

import shelter.patterns.factory.StrategyFactory;
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
    private String passwordHash = "";

    public User(int id, String name, String email, String phone, List<String> preferredChannels) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.phone = phone;
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
        // STRATEGY: one service, and the strategy is switched at runtime for each channel.
        NotificationService service = new NotificationService(StrategyFactory.createNotification(preferredChannels.get(0)));
        for (String channel : preferredChannels) {
            service.setStrategy(StrategyFactory.createNotification(channel));
            service.notify(this, message);
        }
    }

    public int getId() { return id; }
    public String getName() { return name; }
    public String getEmail() { return email; }
    public String getPhone() { return phone; }
    public List<String> getPreferredChannels() { return new ArrayList<>(preferredChannels); }
    public String getPasswordHash() { return passwordHash; }

    public void setName(String name) { this.name = name; }
    public void setEmail(String email) { this.email = email; }
    public void setPhone(String phone) { this.phone = phone; }
    public void setPasswordHash(String passwordHash) { this.passwordHash = passwordHash == null ? "" : passwordHash; }
    public void setPreferredChannels(List<String> preferredChannels) {
        this.preferredChannels = atLeastOneChannel(preferredChannels);
    }

    @Override
    public String toString() {
        return "#" + id + " | " + getRole() + " | " + name + " | " + email + " | " + phone
                + " | Notify by: " + String.join(", ", preferredChannels) + getExtraDetails();
    }
}
