package service;

import dataAccess.GameDAO;
import dataAccess.AuthDAO;
import chess.ChessGame;
import model.GameData;
import exceptions.AuthenticationException;
import result.GameCreationResult;

public class GameService {
    private final GameDAO gameDAO;
    private final AuthDAO authDAO;

    public GameService(GameDAO gameDAO, AuthDAO authDAO) {
        this.gameDAO = gameDAO;
        this.authDAO = authDAO;
    }

    public GameCreationResult createGame(String authToken, String gameName, String blackUsername, String whiteUsername, ChessGame chessGame) throws AuthenticationException {
        // Validate the authToken
        if (!authDAO.isValidToken(authToken)) {
            throw new AuthenticationException("Invalid or expired authToken.");
        }

        try {
            // Proceed with game creation
            int gameId = 0;
            gameId = gameDAO.createGame(gameName, blackUsername, whiteUsername, chessGame);

            // Assuming createGame returns the game ID on success
            if (gameId > 0) {
                return new GameCreationResult(true, "Game created successfully", gameId);
            } else {
                // If gameId <= 0, it indicates failure (based on our assumption)
                return new GameCreationResult(false, "Failed to create game", -1);
            }
        } catch (Exception e) {
            // If there's an exception, consider it a failure to create the game
            // Log the exception or handle it as needed
            return new GameCreationResult(false, "Failed to create game due to an error: " + e.getMessage(), -1);
        }
    }

}
