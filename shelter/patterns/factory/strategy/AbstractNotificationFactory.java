package shelter.patterns.factory.strategy;

import shelter.patterns.strategy.NotificationStrategy;

public abstract class AbstractNotificationFactory {
    public abstract String getChannel();

    public abstract NotificationStrategy createNotification();

    @Override
    public final String toString() {
        return getChannel();
    }
}
