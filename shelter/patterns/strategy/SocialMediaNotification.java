package shelter.patterns.strategy;

import shelter.model.User;

public class SocialMediaNotification implements NotificationStrategy {
    @Override
    public void send(User to, String message) {
        System.out.println("  [Social media message to @" + to.getName().replace(" ", "") + "] " + message);
    }

    // Outreach: a public post on the shelter's own page (not to one user).
    public void postToPage(String post) {
        System.out.println("  [Posted on the shelter's Facebook & Instagram page] " + post);
    }
}
