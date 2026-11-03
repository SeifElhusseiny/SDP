package shelter.patterns.factory.strategy;

import shelter.patterns.strategy.CashPayment;
import shelter.patterns.strategy.PaymentStrategy;

public class CashPaymentFactory extends AbstractPaymentFactory {
    @Override
    public String getPaymentMethod() {
        return "Cash";
    }

    @Override
    public PaymentStrategy createPayment() {
        return new CashPayment();
    }
}
