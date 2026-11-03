package shelter.patterns.factory.user;

import java.util.List;

import shelter.model.User;
import shelter.patterns.factory.strategy.AbstractNotificationFactory;

public abstract class AbstractUserFactory {
    private final List<AbstractNotificationFactory> notificationFactories;

    protected AbstractUserFactory(List<AbstractNotificationFactory> notificationFactories) {
        this.notificationFactories = notificationFactories;
    }

    public abstract String getUserType();

    public final User createUser(int id, String name, String email, String phone, List<String> channels) {
        return createUser(id, name, email, phone, channels, null);
    }

    public final User createUser(int id, String name, String email, String phone, List<String> channels,
            String passwordHash) {
        User user = createUserInstance(id, name, email, phone, channels);
        if (passwordHash != null) {
            user.setPasswordHash(passwordHash);
        }
        return user;
    }

    protected abstract User createUserInstance(int id, String name, String email, String phone,
            List<String> channels);

    protected final List<AbstractNotificationFactory> getNotificationFactories() {
        return notificationFactories;
    }

    @Override
    public final String toString() {
        return getUserType();
    }
}
