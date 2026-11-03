package shelter.patterns.factory.strategy;

import shelter.patterns.strategy.NotificationStrategy;
import shelter.patterns.strategy.SocialMediaNotification;

public class SocialMediaNotificationFactory extends AbstractNotificationFactory {
    @Override
    public String getChannel() {
        return "Social media";
    }

    @Override
    public NotificationStrategy createNotification() {
        return new SocialMediaNotification();
    }
}
