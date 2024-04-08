package webSocketMessages.serverMessages;

public class Error extends ServerMessage{

    private  String errorMessage;

    public Error(ServerMessageType type, String errorMessage){
        super(type);
        this.serverMessageType = ServerMessageType.ERROR;
        this.errorMessage = errorMessage;
    }

    public Error(ServerMessageType type){
        super(type);
    }

    public String getErrorMessage(){
        return errorMessage;
    }
}

