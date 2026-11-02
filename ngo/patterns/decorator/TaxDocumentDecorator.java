package ngo.patterns.decorator;

public class TaxDocumentDecorator extends ReceiptDecorator {
    public TaxDocumentDecorator(DonationReceipt wrappedReceipt) {
        super(wrappedReceipt);
    }

    @Override
    public String getDescription() {
        return wrappedReceipt.getDescription() + " + tax document";
    }

    @Override
    public double getAmount() {
        return wrappedReceipt.getAmount();
    }
}
