package ngo.patterns.strategy;

public interface CommunicationStrategy {
    void send(String recipient, String message);
}
