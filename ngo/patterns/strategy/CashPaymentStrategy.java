package ngo.patterns.strategy;

public class CashPaymentStrategy implements PaymentStrategy {
    @Override
    public String processPayment(String donorName, double amount) {
        return "Cash payment received from " + donorName + " for amount " + amount + ".";
    }
}
