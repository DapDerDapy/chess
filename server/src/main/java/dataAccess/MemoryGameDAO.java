package dataAccess;
import chess.ChessGame;
import model.GameData;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.Collection;

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


    public Collection<GameData> listGames() {
        return new ArrayList<>(gameInfo); // Return a copy of the gameInfo list
    }

    public boolean updateGame(int gameID, ChessGame updatedChessGame) {
        for (int i = 0; i < gameInfo.size(); i++) {
            GameData currentGame = gameInfo.get(i);
            if (currentGame.id() == gameID) {
                // Create a new GameData instance with the updated ChessGame
                GameData updatedGame = new GameData(
                        currentGame.id(),
                        currentGame.blackUsername(),
                        currentGame.whiteUsername(),
                        currentGame.gameName(),
                        updatedChessGame);
                // Replace the old GameData with the updated one
                gameInfo.set(i, updatedGame);
                return true; // Update successful
            }
        }
        return false; // GameID not found, update unsuccessful
    }

    public void clearAll(){
        gameInfo.clear();
    }
}
