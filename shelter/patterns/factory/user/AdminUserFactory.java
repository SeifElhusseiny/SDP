package shelter.patterns.factory.user;

import java.util.List;

import shelter.model.Admin;
import shelter.model.User;
import shelter.patterns.factory.strategy.AbstractNotificationFactory;

public class AdminUserFactory extends AbstractUserFactory {
    public AdminUserFactory(List<AbstractNotificationFactory> notificationFactories) {
        super(notificationFactories);
    }

    @Override
    public String getUserType() {
        return "Admin";
    }

    @Override
    protected User createUserInstance(int id, String name, String email, String phone, List<String> channels) {
        return new Admin(id, name, email, phone, channels, getNotificationFactories());
    }
}
