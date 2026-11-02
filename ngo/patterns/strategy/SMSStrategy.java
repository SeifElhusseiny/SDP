package ngo.patterns.strategy;

public class SMSStrategy implements CommunicationStrategy {
    @Override
    public void send(String recipient, String message) {
        System.out.println("[SMS] to " + recipient + ": " + message);
    }
}
