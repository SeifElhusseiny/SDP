package shelter.patterns.strategy;

public class ChequePayment implements PaymentStrategy {
    @Override
    public String[] getRequiredDetails() {
        return new String[] {"Bank name", "Cheque number (6 digits)"};
    }

    @Override
    public boolean isValidDetail(int index, String value) {
        switch (index) {
            case 0: return value.matches("[A-Za-z ]+");
            case 1: return value.matches("\\d{6}");
            default: return false;
        }
    }

    @Override
    public String pay(String payerName, double amount, String[] details) {
        return "Cheque #" + details[1] + " (" + details[0] + ") for " + amount
                + " EGP recorded. It will be counted once the cheque clears.";
    }

    @Override
    public String getReference(String[] details) {
        return "Cheque #" + details[1];
    }
}
