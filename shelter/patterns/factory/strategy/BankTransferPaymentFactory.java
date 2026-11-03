package shelter.patterns.factory.strategy;

import shelter.patterns.strategy.BankTransferPayment;
import shelter.patterns.strategy.PaymentStrategy;

public class BankTransferPaymentFactory extends AbstractPaymentFactory {
    @Override
    public String getPaymentMethod() {
        return "Bank transfer";
    }

    @Override
    public PaymentStrategy createPayment() {
        return new BankTransferPayment();
    }
}
