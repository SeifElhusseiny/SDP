package shelter.patterns.strategy;

public class CashPayment implements PaymentStrategy {
    @Override
    public String[] getRequiredDetails() {
        return new String[0];
    }

    @Override
    public boolean isValidDetail(int index, String value) {
        return true;
    }

    @Override
    public String pay(String payerName, double amount, String[] details) {
        return "Cash payment of " + amount + " EGP received from " + payerName + " at the shelter desk.";
    }

    @Override
    public String getReference(String[] details) {
        return "Cash";
    }
}
