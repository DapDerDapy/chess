package webSocketMessages.userCommands;

public class Resign extends UserGameCommand{
    private int gameID;

    public Resign(String authToken, int gameID){
        super(authToken);
        this.commandType = CommandType.LEAVE;
        this.gameID = gameID;
    }

    public Resign(String authToken){
        super(authToken);
    }

    public int getGameID(){ return gameID; }

}
