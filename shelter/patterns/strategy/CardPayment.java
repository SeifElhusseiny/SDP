package shelter.patterns.strategy;

public class CardPayment implements PaymentStrategy {
    @Override
    public String[] getRequiredDetails() {
        return new String[] {"Cardholder name", "Card number (16 digits)", "Expiry date (MM/YY)"};
    }

    @Override
    public boolean isValidDetail(int index, String value) {
        switch (index) {
            case 0: return value.matches("[A-Za-z ]+");
            case 1: return value.matches("\\d{16}");
            case 2: return value.matches("(0[1-9]|1[0-2])/\\d{2}");
            default: return false;
        }
    }

    @Override
    public String pay(String payerName, double amount, String[] details) {
        return "Card payment of " + amount + " EGP approved. Charged to " + details[0]
                + "'s card ending in " + lastFour(details[1]) + ".";
    }

    @Override
    public String getReference(String[] details) {
        return "Card ****" + lastFour(details[1]);
    }

    private String lastFour(String cardNumber) {
        return cardNumber.substring(cardNumber.length() - 4);
    }
}
