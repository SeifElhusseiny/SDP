package shelter.patterns.strategy;

import shelter.model.User;

// STRATEGY pattern: one way of sending a message (Email, SMS, Social media).
public interface NotificationStrategy {
    void send(User to, String message);
}
