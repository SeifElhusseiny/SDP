package shelter.patterns.strategy;

import shelter.model.User;

public class EmailNotification implements NotificationStrategy {
    @Override
    public void send(User to, String message) {
        System.out.println("  [Email to " + to.getEmail() + "] " + message);
    }
}
