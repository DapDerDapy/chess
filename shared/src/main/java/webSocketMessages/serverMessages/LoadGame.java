package webSocketMessages.serverMessages;

public class LoadGame extends ServerMessage{

    private String game;
    public LoadGame (ServerMessageType type, String game){
        super(type);
        this.serverMessageType = ServerMessageType.LOAD_GAME;
        this.game = "game";
    }

    public LoadGame(ServerMessageType type){
        super(type);
    }

    public String getGame(){ return game; }


}
