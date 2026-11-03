package shelter.patterns.factory.user;

import java.util.List;

import shelter.model.Adopter;
import shelter.model.User;
import shelter.patterns.factory.strategy.AbstractNotificationFactory;

public class AdopterUserFactory extends AbstractUserFactory {
    public AdopterUserFactory(List<AbstractNotificationFactory> notificationFactories) {
        super(notificationFactories);
    }

    @Override
    public String getUserType() {
        return "Adopter";
    }

    @Override
    protected User createUserInstance(int id, String name, String email, String phone, List<String> channels) {
        return new Adopter(id, name, email, phone, channels, getNotificationFactories());
    }
}
