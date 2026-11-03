package shelter.patterns.factory.strategy;

import shelter.patterns.strategy.ChequePayment;
import shelter.patterns.strategy.PaymentStrategy;

public class ChequePaymentFactory extends AbstractPaymentFactory {
    @Override
    public String getPaymentMethod() {
        return "Cheque";
    }

    @Override
    public PaymentStrategy createPayment() {
        return new ChequePayment();
    }
}
