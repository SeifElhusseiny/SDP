package shelter.patterns.factory.strategy;

import shelter.patterns.strategy.EmailNotification;
import shelter.patterns.strategy.NotificationStrategy;

public class EmailNotificationFactory extends AbstractNotificationFactory {
    @Override
    public String getChannel() {
        return "Email";
    }

    @Override
    public NotificationStrategy createNotification() {
        return new EmailNotification();
    }
}
