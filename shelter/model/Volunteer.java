package shelter.model;

import java.time.LocalDate;
import java.util.List;

import shelter.patterns.factory.strategy.AbstractNotificationFactory;

public class Volunteer extends User {
    private String skill = "-";
    private String availability = "-";
    private double hoursWorked;

    public Volunteer(int id, String name, String email, String phone, List<String> channels,
            List<AbstractNotificationFactory> notificationFactories) {
        super(id, name, email, phone, channels, notificationFactories);
    }

    @Override
    public String getRole() { return "Volunteer"; }

    public void logHours(double hours) { hoursWorked += hours; }

    public String generateCertificate() {
        return "**************** VOLUNTEER CERTIFICATE ****************\n"
                + " Paws & Whiskers Animal Shelter proudly certifies that\n"
                + "     " + getName() + "\n"
                + " volunteered " + hoursWorked + " hours as a " + skill + " volunteer.\n"
                + " Thank you for your time and care for our animals.\n"
                + " Issued on: " + LocalDate.now() + "\n"
                + "********************************************************";
    }

    public String getSkill() { return skill; }
    public String getAvailability() { return availability; }
    public double getHoursWorked() { return hoursWorked; }
    public void setHoursWorked(double hoursWorked) { this.hoursWorked = hoursWorked; }
    public void setSkill(String skill) { this.skill = skill; }
    public void setAvailability(String availability) { this.availability = availability; }

    @Override
    public String getExtraDetails() {
        return " | Skill: " + skill + " | Available: " + availability + " | Hours: " + hoursWorked;
    }
}
