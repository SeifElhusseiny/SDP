package shelter.patterns.strategy;

// STRATEGY pattern: one way of paying a money donation.
// Each payment method asks for its own details, checks them its own way,
// and processes the payment differently.
public interface PaymentStrategy {
    // The details this method needs from the donor (empty for cash).
    String[] getRequiredDetails();

    // Checks one detail (index matches getRequiredDetails()).
    boolean isValidDetail(int index, String value);

    // Processes the payment and returns a confirmation message.
    String pay(String payerName, double amount, String[] details);

    // A short, safe summary saved with the donation (never the full card number).
    String getReference(String[] details);
}
