package dataAccess;
import chess.ChessGame;
import model.GameData;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class MemoryGameDAO implements GameDAO {
    private final List<GameData> gameInfo = new ArrayList<>();
    private final AtomicInteger gameIdCounter = new AtomicInteger();





    public void createGame(String blackUsername, String whiteUsername, String gameName, ChessGame chessGame){
        int gameId = gameIdCounter.incrementAndGet();
        GameData game = new GameData(gameId, blackUsername, whiteUsername, gameName, chessGame);
    }

}
