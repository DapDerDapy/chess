package dataAccess;

import chess.ChessGame;
import model.GameData;
import request.JoinGameRequest;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;

public class MemoryGameDAO implements GameDAO {
    private final List<GameData> gameInfo = new ArrayList<>();
    private final AtomicInteger gameIdCounter = new AtomicInteger();
    public int createGame(String gameName, String blackUsername, String whiteUsername, ChessGame chessGame){

        int gameId = gameIdCounter.incrementAndGet();
        GameData newGame = new GameData(gameId, blackUsername, whiteUsername, gameName, chessGame);
        gameInfo.add(newGame);
        return gameId;
    }

    public boolean isColorTaken(int gameID, String color) {
        for (GameData game : gameInfo) {
            if (game.getGameID() == gameID) {
                if ("BLACK".equals(color)) {
                    if (game.getBlackUsername() != null)
                        return true;
                } else if ("WHITE".equals(color)) {
                    if (game.getWhiteUsername() != null)
                        return true;
                }
                break; // Exit the loop once the game is found
            }
        }
        return false; // Default return value if gameID is not found or color does not match
    }

    @Override
    public boolean rejoinPlayer(int gameID, String color, String username) {
        return false;
    }

    @Override
    public boolean joinObserverChecks(int gameId, String authToken) {
        return false;
    }


    public GameData getGame(int gameID) {
        Optional<GameData> match = gameInfo.stream()
                .filter(game -> game.getGameID() == gameID)
                .findFirst();
        return match.orElse(null);
    }

    @Override
    public boolean joinGame(int gameID, String color, String authToken, String username) {
        GameData game = getGame(gameID); // Implement this method to find a game by ID
        if (game == null) {
            return false; // Game not found
        }

        synchronized (game) { // Ensure thread safety if needed
            if (color != null && !color.isEmpty()) {
                // Trying to join as a player
                  if (game.getBlackUsername() == null && color.equals("BLACK")) {
                        game.setBlackUsername(username);
                        return true;
                  }
                  if (game.getWhiteUsername() == null && color.equals("WHITE")) {
                      game.setWhiteUsername(username);
                      return true;
                  }
            } else if (isColorTaken(gameID, color)) {
                return false;
            } else {
                return true;
            }
        }
        return false;
    }

    public ArrayList<GameData> listGames() {
        return new ArrayList<>(gameInfo); // Return a copy of the gameInfo list
    }

    public boolean updateGame(int gameID, ChessGame updatedChessGame) {
        for (int i = 0; i < gameInfo.size(); i++) {
            GameData currentGame = gameInfo.get(i);
            if (currentGame.getGameID() == gameID) {
                // Create a new GameData instance with the updated ChessGame
                GameData updatedGame = new GameData(
                        currentGame.getGameID(),
                        currentGame.getBlackUsername(),
                        currentGame.getWhiteUsername(),
                        currentGame.getGameName(),
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
