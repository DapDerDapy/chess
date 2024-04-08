package webSocketMessages.userCommands;
import chess.ChessMove;

public class MakeMove extends UserGameCommand{

    private int gameID;
    private ChessMove move;

    public MakeMove(String authToken, int gameID, ChessMove move){
        super(authToken);
        this.commandType = CommandType.MAKE_MOVE;
        this.gameID = gameID;
        this.move = move;
    }


    public MakeMove(String authToken){
        super(authToken);
    }

    public int getGameID() { return gameID; }

    public ChessMove getMove() {return move;}
}
