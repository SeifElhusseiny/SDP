package shelter.patterns.strategy;

public class BankTransferPayment implements PaymentStrategy {
    @Override
    public String[] getRequiredDetails() {
        return new String[] {"Bank name", "Account number (10-20 digits)"};
    }

    @Override
    public boolean isValidDetail(int index, String value) {
        switch (index) {
            case 0: return value.matches("[A-Za-z ]+");
            case 1: return value.matches("\\d{10,20}");
            default: return false;
        }
    }

    @Override
    public String pay(String payerName, double amount, String[] details) {
        return "Bank transfer of " + amount + " EGP from " + details[0] + " account ending in "
                + lastFour(details[1]) + " is pending confirmation (1-2 working days).";
    }

    @Override
    public String getReference(String[] details) {
        return "Bank transfer (" + details[0] + " ****" + lastFour(details[1]) + ")";
    }

    private String lastFour(String accountNumber) {
        return accountNumber.substring(accountNumber.length() - 4);
    }
}
