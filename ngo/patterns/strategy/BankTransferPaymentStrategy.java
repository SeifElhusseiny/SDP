package ngo.patterns.strategy;

public class BankTransferPaymentStrategy implements PaymentStrategy {
    @Override
    public String processPayment(String donorName, double amount) {
        return "Bank transfer initiated for " + donorName + ". Amount " + amount + " is pending confirmation.";
    }
}
