package ngo.patterns.decorator;

public class ThankYouNoteDecorator extends ReceiptDecorator {
    public ThankYouNoteDecorator(DonationReceipt wrappedReceipt) {
        super(wrappedReceipt);
    }

    @Override
    public String getDescription() {
        return wrappedReceipt.getDescription() + " + thank-you note";
    }

    @Override
    public double getAmount() {
        return wrappedReceipt.getAmount();
    }
}
