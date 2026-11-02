package ngo.patterns.decorator;

public abstract class ReceiptDecorator implements DonationReceipt {
    protected final DonationReceipt wrappedReceipt;

    protected ReceiptDecorator(DonationReceipt wrappedReceipt) {
        this.wrappedReceipt = wrappedReceipt;
    }
}
