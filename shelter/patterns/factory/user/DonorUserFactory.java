package shelter.patterns.factory.user;

import java.util.List;

import shelter.model.Donor;
import shelter.model.User;
import shelter.patterns.factory.strategy.AbstractNotificationFactory;

public class DonorUserFactory extends AbstractUserFactory {
    public DonorUserFactory(List<AbstractNotificationFactory> notificationFactories) {
        super(notificationFactories);
    }

    @Override
    public String getUserType() {
        return "Donor";
    }

    @Override
    protected User createUserInstance(int id, String name, String email, String phone, List<String> channels) {
        return new Donor(id, name, email, phone, channels, getNotificationFactories());
    }
}
