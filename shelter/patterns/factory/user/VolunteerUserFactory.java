package shelter.patterns.factory.user;

import java.util.List;

import shelter.model.User;
import shelter.model.Volunteer;
import shelter.patterns.factory.strategy.AbstractNotificationFactory;

public class VolunteerUserFactory extends AbstractUserFactory {
    public VolunteerUserFactory(List<AbstractNotificationFactory> notificationFactories) {
        super(notificationFactories);
    }

    @Override
    public String getUserType() {
        return "Volunteer";
    }

    @Override
    protected User createUserInstance(int id, String name, String email, String phone, List<String> channels) {
        return new Volunteer(id, name, email, phone, channels, getNotificationFactories());
    }
}
