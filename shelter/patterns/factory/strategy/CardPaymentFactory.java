package shelter.patterns.factory.strategy;

import shelter.patterns.strategy.CardPayment;
import shelter.patterns.strategy.PaymentStrategy;

public class CardPaymentFactory extends AbstractPaymentFactory {
    @Override
    public String getPaymentMethod() {
        return "Card";
    }

    @Override
    public PaymentStrategy createPayment() {
        return new CardPayment();
    }
}
