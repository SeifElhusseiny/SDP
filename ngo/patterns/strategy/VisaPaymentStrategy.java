package ngo.patterns.strategy;

public class VisaPaymentStrategy implements PaymentStrategy {
    @Override
    public String processPayment(String donorName, double amount) {
        return "Visa payment approved for " + donorName + ". Amount " + amount + " successfully charged.";
    }
}
