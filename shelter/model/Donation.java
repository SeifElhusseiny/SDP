package shelter.model;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import shelter.patterns.observer.Observer;
import shelter.patterns.observer.Subject;

// A donation is an OBSERVER Subject: when it is received it notifies
// the donor (thank-you) and every admin (new donation alert).
public class Donation implements Subject {
    private final int id;
    private final Donor donor;
    private String type;
    private double amount;
    private final String paymentMethod;
    private final LocalDate date;
    private final List<Observer> observers = new ArrayList<>();

    public Donation(int id, Donor donor, String type, double amount, String paymentMethod) {
        this.id = id;
        this.donor = donor;
        this.type = type;
        this.amount = amount;
        this.paymentMethod = paymentMethod;
        this.date = LocalDate.now();
    }

    // Used when loading a saved donation (keeps its original date).
    public Donation(int id, Donor donor, String type, double amount, String paymentMethod, LocalDate date) {
        this.id = id;
        this.donor = donor;
        this.type = type;
        this.amount = amount;
        this.paymentMethod = paymentMethod;
        this.date = date;
    }

    public boolean isMoney() {
        return !paymentMethod.equals("In-kind");
    }

    // A printable receipt for the donor.
    public String generateReceipt() {
        return "------------------- DONATION RECEIPT -------------------\n"
                + " Paws & Whiskers Animal Shelter (registered non-profit)\n"
                + " Receipt no.: R-" + String.format("%04d", id) + "      Date: " + date + "\n"
                + " Donor: " + donor.getName() + " (" + donor.getEmail() + ")\n"
                + " Donation: " + type + "\n"
                + " " + (isMoney() ? "Amount" : "Estimated value") + ": " + amount + " EGP\n"
                + " Paid by: " + paymentMethod + "\n"
                + " Thank you for helping our animals!\n"
                + "--------------------------------------------------------";
    }

    @Override
    public void attach(Observer observer) {
        if (!observers.contains(observer)) {
            observers.add(observer);
        }
    }

    @Override
    public void detach(Observer observer) {
        observers.remove(observer);
    }

    @Override
    public void notifyObservers(String message) {
        for (Observer observer : observers) {
            observer.update(message);
        }
    }

    public int getId() { return id; }
    public Donor getDonor() { return donor; }
    public String getType() { return type; }
    public double getAmount() { return amount; }
    public String getPaymentMethod() { return paymentMethod; }
    public LocalDate getDate() { return date; }
    public void setType(String type) { this.type = type; }
    public void setAmount(double amount) { this.amount = amount; }

    @Override
    public String toString() {
        return "#" + id + " | " + date + " | Donor: " + donor.getName() + " | " + type + " | " + amount + " EGP | Paid by: " + paymentMethod;
    }
}
