package ngo.model;

public class CommunicationMessage {
    private final int id;
    private final String recipient;
    private final String channel;
    private final String message;

    public CommunicationMessage(int id, String recipient, String channel, String message) {
        this.id = id;
        this.recipient = recipient;
        this.channel = channel;
        this.message = message;
    }

    public int getId() {
        return id;
    }

    public String getRecipient() {
        return recipient;
    }

    public String getChannel() {
        return channel;
    }

    public String getMessage() {
        return message;
    }

    @Override
    public String toString() {
        return "Communication #" + id + " | To: " + recipient + " | Channel: " + channel + " | " + message;
    }
}
