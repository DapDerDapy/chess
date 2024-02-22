package dataAccess;
import chess.ChessGame;
import model.GameData;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;

public class MemoryGameDAO implements GameDAO {
    private final List<GameData> gameInfo = new ArrayList<>();
    private final AtomicInteger gameIdCounter = new AtomicInteger();

    public void createGame(String gameName, String blackUsername, String whiteUsername, ChessGame chessGame){
        int gameId = gameIdCounter.incrementAndGet();
        GameData newGame = new GameData(gameId, blackUsername, whiteUsername, gameName, chessGame);
        gameInfo.add(newGame);
    }

    public GameData getGame(int gameID) {
        Optional<GameData> match = gameInfo.stream()
                .filter(game -> game.id() == gameID)
                .findFirst();
        return match.orElse(null);
    }



}
