package ngo.patterns.strategy;

public class SocialMediaStrategy implements CommunicationStrategy {
    @Override
    public void send(String recipient, String message) {
        System.out.println("[Social Media] post to " + recipient + ": " + message);
    }
}
