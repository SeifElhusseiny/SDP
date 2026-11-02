package ngo.patterns.strategy;

public class ChequePaymentStrategy implements PaymentStrategy {
    @Override
    public String processPayment(String donorName, double amount) {
        return "Cheque payment recorded for " + donorName + ". Amount " + amount + " awaiting clearance.";
    }
}
