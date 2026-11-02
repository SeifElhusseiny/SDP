package shelter.patterns.strategy;

import shelter.model.User;

// STRATEGY pattern - the Context: holds one strategy and can switch it at runtime.
public class NotificationService {
    private NotificationStrategy strategy;

    public NotificationService(NotificationStrategy strategy) {
        this.strategy = strategy;
    }

    public void setStrategy(NotificationStrategy strategy) {
        this.strategy = strategy;
    }

    public void notify(User to, String message) {
        strategy.send(to, message);
    }
}
