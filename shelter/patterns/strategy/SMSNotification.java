package shelter.patterns.strategy;

import shelter.model.User;

public class SMSNotification implements NotificationStrategy {
    @Override
    public void send(User to, String message) {
        System.out.println("  [SMS to " + to.getPhone() + "] " + message);
    }
}
