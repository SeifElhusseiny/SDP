package shelter.patterns.strategy;

public class BankTransferPayment implements PaymentStrategy {
    @Override
    public String pay(String payerName, double amount) {
        return "Bank transfer of " + amount + " EGP from " + payerName + " is pending confirmation.";
    }
}
