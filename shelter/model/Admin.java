package shelter.model;

import java.util.List;

import shelter.patterns.factory.strategy.AbstractNotificationFactory;

public class Admin extends User {
    public Admin(int id, String name, String email, String phone, List<String> channels,
            List<AbstractNotificationFactory> notificationFactories) {
        super(id, name, email, phone, channels, notificationFactories);
    }

    @Override
    public String getRole() { return "Admin"; }
}
