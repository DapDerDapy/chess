package webSocketMessages.serverMessages;

public class Notification extends ServerMessage{

    private String notification;

    public Notification(ServerMessageType type, String notification){
        super(type);
        this.serverMessageType = ServerMessageType.NOTIFICATION;
        this.notification = notification;
    }

    public Notification(ServerMessageType type){
        super(type);
    }

    public String getNotification(){ return notification; }
}
