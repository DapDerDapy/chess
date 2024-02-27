package dataAccess;
import chess.ChessGame;
import model.GameData;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.Collection;
import wrappers.*;

public class MemoryGameDAO implements GameDAO {
    private final List<GameData> gameInfo = new ArrayList<>();
    private final AtomicInteger gameIdCounter = new AtomicInteger();

    private String blackUsername;
    private String whiteUsername;

    public int createGame(String gameName, String blackUsername, String whiteUsername, ChessGame chessGame){

        this.blackUsername = blackUsername;
        this.whiteUsername = whiteUsername;

        int gameId = gameIdCounter.incrementAndGet();
        GameData newGame = new GameData(gameId, blackUsername, whiteUsername, gameName, chessGame);
        gameInfo.add(newGame);
        return gameId;
    }

    public boolean isColorTaken(int gameID, String color) {
        for (GameData game : gameInfo) {
            if (game.id() == gameID) { // Assuming GameData has a getter for gameId
                if ("black".equals(color)) {
                    if (game.blackUsername() != null)
                        return true;
                } else if ("white".equals(color)) {
                    if (game.whiteUsername() != null)
                        return true;
                    //return game.whiteUsername() != null;
                }
                break; // Exit the loop once the game is found
            }
        }
        return false; // Default return value if gameID is not found or color does not match
    }


    public GameData getGame(int gameID) {
        Optional<GameData> match = gameInfo.stream()
                .filter(game -> game.id() == gameID)
                .findFirst();
        return match.orElse(null);
    }

    @Override
    public boolean joinGame(int gameID, String color, String authToken, String username) {
        // Example implementation - adjust according to your actual data structure
        GameData game = getGame(gameID); // Implement this method to find a game by ID
        if (game == null) {
            return false; // Game not found
        }

        synchronized (game) { // Ensure thread safety if needed
            if (color != null && !color.isEmpty()) {
                // Trying to join as a player
                  if (game.blackUsername() == null && color.equals("BLACK")) {
                    return true;
                  }
                  if (game.whiteUsername() == null && color.equals("WHITE")) {
                    return true;
                  }
            } else if (isColorTaken(gameID, color)) {
                return false;
            } else {
                // Joining as an observer
                //game.addObserver(authToken); // Implement this method
                return true;
            }
        }
        return false;
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
