package shelter.patterns.factory.strategy;

import shelter.patterns.strategy.NotificationStrategy;
import shelter.patterns.strategy.SMSNotification;

public class SMSNotificationFactory extends AbstractNotificationFactory {
    @Override
    public String getChannel() {
        return "SMS";
    }

    @Override
    public NotificationStrategy createNotification() {
        return new SMSNotification();
    }
}
