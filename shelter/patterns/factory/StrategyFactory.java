package shelter.patterns.factory;

import shelter.patterns.strategy.BankTransferPayment;
import shelter.patterns.strategy.CardPayment;
import shelter.patterns.strategy.CashPayment;
import shelter.patterns.strategy.ChequePayment;
import shelter.patterns.strategy.EmailNotification;
import shelter.patterns.strategy.NotificationStrategy;
import shelter.patterns.strategy.PaymentStrategy;
import shelter.patterns.strategy.SMSNotification;
import shelter.patterns.strategy.SocialMediaNotification;

// FACTORY pattern: picks the right strategy object from a name.
public class StrategyFactory {
    public static final String[] CHANNELS = {"Email", "SMS", "Social media"};
    public static final String[] PAYMENT_METHODS = {"Cash", "Card", "Bank transfer", "Cheque"};

    public static NotificationStrategy createNotification(String channel) {
        switch (channel) {
            case "SMS":
                return new SMSNotification();
            case "Social media":
                return new SocialMediaNotification();
            default:
                return new EmailNotification();
        }
    }

    public static PaymentStrategy createPayment(String method) {
        switch (method) {
            case "Card":
                return new CardPayment();
            case "Bank transfer":
                return new BankTransferPayment();
            case "Cheque":
                return new ChequePayment();
            default:
                return new CashPayment();
        }
    }
}
