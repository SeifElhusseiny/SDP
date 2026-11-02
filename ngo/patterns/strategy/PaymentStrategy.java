package ngo.patterns.strategy;

public interface PaymentStrategy {
    String processPayment(String donorName, double amount);
}
