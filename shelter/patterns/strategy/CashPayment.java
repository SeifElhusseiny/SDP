package shelter.patterns.strategy;

public class CashPayment implements PaymentStrategy {
    @Override
    public String pay(String payerName, double amount) {
        return "Cash payment of " + amount + " EGP received from " + payerName + ".";
    }
}
