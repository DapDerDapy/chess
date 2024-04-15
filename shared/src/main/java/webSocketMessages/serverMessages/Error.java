package webSocketMessages.serverMessages;

public class Error extends ServerMessage {

    private final String errorMessage;

    public Error(ServerMessageType type, String errorMessage) {
        super(type);
        this.serverMessageType = ServerMessageType.ERROR;
        this.errorMessage = "error";
    }

    public String getErrorMessage() {
        return errorMessage;
    }
}
