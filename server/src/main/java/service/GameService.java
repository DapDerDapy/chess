package service;

import dataAccess.GameDAO;
import dataAccess.AuthDAO;
import chess.ChessGame;
import model.GameData;
import exceptions.AuthenticationException;

public class GameService {
    private final GameDAO gameDAO;
    private final AuthDAO authDAO;

    public GameService(GameDAO gameDAO, AuthDAO authDAO) {
        this.gameDAO = gameDAO;
        this.authDAO = authDAO;
    }

    public GameData createGame(String authToken, String gameName, String blackUsername, String whiteUsername, ChessGame chessGame) throws AuthenticationException {
        // Validate the authToken
        if (!authDAO.isValidToken(authToken)) {
            throw new AuthenticationException("Invalid or expired authToken.");
        }

        // Proceed with game creation
        gameDAO.createGame(gameName, blackUsername, whiteUsername, chessGame);

        // Assuming you want to return the newly created game details
        // This step might involve fetching the game by some unique attribute (e.g., name and usernames)
        // For simplicity, let's assume gameName is unique and use it to find the game
        // Note: This is a simplification. In a real scenario, you'd need a robust way to fetch the newly created game
        return gameDAO.listGames().stream()
                .filter(game -> game.gameName().equals(gameName) &&
                        game.blackUsername().equals(blackUsername) &&
                        game.whiteUsername().equals(whiteUsername))
                .findFirst()
                .orElse(null);
    }
}
