package webSocketMessages.userCommands;
import chess.ChessGame;

public class JoinObserver extends UserGameCommand {

    private int gameID;

    public JoinObserver(String authToken, int gameID){
        super(authToken);
        this.commandType = CommandType.JOIN_OBSERVER;
        this.gameID = gameID;
    }

    public JoinObserver(String authToken){
        super(authToken);
    }

    public int getGameID(){ return gameID; }

    public String getAuthToken() {
        return super.getAuthToken();
    }

}
