package webSocketMessages.serverMessages;

public class Notification extends ServerMessage {
    private String message;

    public Notification(ServerMessageType type, String message) {
        super(ServerMessageType.NOTIFICATION);
        this.message = message;
    }

    public String getNotification() {
        return message;
    }
}
