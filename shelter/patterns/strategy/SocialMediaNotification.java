package shelter.patterns.strategy;

import shelter.model.User;

public class SocialMediaNotification implements NotificationStrategy {
    @Override
    public void send(User to, String message) {
        System.out.println("  [Social media message to @" + to.getName().replace(" ", "") + "] " + message);
    }
}
