package shelter.patterns.factory.strategy;

import shelter.patterns.strategy.PaymentStrategy;

public abstract class AbstractPaymentFactory {
    public abstract String getPaymentMethod();

    public abstract PaymentStrategy createPayment();

    @Override
    public final String toString() {
        return getPaymentMethod();
    }
}
