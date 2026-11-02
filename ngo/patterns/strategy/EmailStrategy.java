package ngo.patterns.strategy;

public class EmailStrategy implements CommunicationStrategy {
    @Override
    public void send(String recipient, String message) {
        System.out.println("[Email] to " + recipient + ": " + message);
    }
}
