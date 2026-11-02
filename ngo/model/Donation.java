package ngo.model;

public class Donation {
    private final int id;
    private final String donorName;
    private final String donorType;
    private final String donationType;
    private final double amount;
    private final String paymentMethod;

    public Donation(int id, String donorName, String donorType, String donationType, double amount, String paymentMethod) {
        this.id = id;
        this.donorName = donorName;
        this.donorType = donorType;
        this.donationType = donationType;
        this.amount = amount;
        this.paymentMethod = paymentMethod;
    }

    public int getId() {
        return id;
    }

    public String getDonorName() {
        return donorName;
    }

    public String getDonorType() {
        return donorType;
    }

    public String getDonationType() {
        return donationType;
    }

    public double getAmount() {
        return amount;
    }

    public String getPaymentMethod() {
        return paymentMethod;
    }

    @Override
    public String toString() {
        return "Donation #" + id + " | " + donorName + " (" + donorType + ") | "
                + donationType + " | " + amount + " | " + paymentMethod;
    }
}
