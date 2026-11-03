package shelter.model;

import java.util.List;

import shelter.patterns.factory.strategy.AbstractNotificationFactory;

public class Donor extends User {
    private double totalDonated;

    public Donor(int id, String name, String email, String phone, List<String> channels,
            List<AbstractNotificationFactory> notificationFactories) {
        super(id, name, email, phone, channels, notificationFactories);
    }

    @Override
    public String getRole() { return "Donor"; }

    public void addToTotal(double amount) { totalDonated += amount; }
    public double getTotalDonated() { return totalDonated; }
    public void setTotalDonated(double totalDonated) { this.totalDonated = totalDonated; }

    @Override
    public String getExtraDetails() {
        return " | Total donated: " + totalDonated + " EGP";
    }
}
