package shelter.patterns.strategy;

// STRATEGY pattern: one way of paying a money donation.
public interface PaymentStrategy {
    String pay(String payerName, double amount);
}
