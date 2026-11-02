package ngo.patterns.decorator;

public class BasicDonationReceipt implements DonationReceipt {
    private final String donorName;
    private final double amount;

    public BasicDonationReceipt(String donorName, double amount) {
        this.donorName = donorName;
        this.amount = amount;
    }

    @Override
    public String getDescription() {
        return "Basic donation receipt for " + donorName;
    }

    @Override
    public double getAmount() {
        return amount;
    }
}
