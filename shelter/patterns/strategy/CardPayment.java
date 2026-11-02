package shelter.patterns.strategy;

public class CardPayment implements PaymentStrategy {
    @Override
    public String pay(String payerName, double amount) {
        return "Card payment of " + amount + " EGP charged to " + payerName + "'s card.";
    }
}
