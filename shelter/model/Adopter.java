package shelter.model;

import java.util.List;

import shelter.patterns.factory.strategy.AbstractNotificationFactory;

public class Adopter extends User {
    private String homeType = "-";

    public Adopter(int id, String name, String email, String phone, List<String> channels,
            List<AbstractNotificationFactory> notificationFactories) {
        super(id, name, email, phone, channels, notificationFactories);
    }

    @Override
    public String getRole() { return "Adopter"; }

    public String getHomeType() { return homeType; }
    public void setHomeType(String homeType) { this.homeType = homeType; }

    @Override
    public String getExtraDetails() {
        return " | Home: " + homeType;
    }
}
