package service;

import dataAccess.GameDAO;
import dataAccess.AuthDAO;
import chess.ChessGame;
import dataAccess.UserDAO;
import model.GameData;
import exceptions.*;
import result.*;
import request.*;
import java.util.Collection;


public class GameService {
    private final GameDAO gameDAO;
    private final AuthDAO authDAO;
    private final UserDAO userDAO;

    public GameService(GameDAO gameDAO, AuthDAO authDAO, UserDAO userDAO) {
        this.gameDAO = gameDAO;
        this.authDAO = authDAO;
        this.userDAO = userDAO;
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

    public Collection<GameData> listGames(String authToken) throws AuthenticationException {
        // Validate the authToken

        if (!authDAO.isValidToken(authToken)) {
            throw new AuthenticationException("Invalid or expired authToken.");
        }

        // Fetch and return all games from the DAO
        return gameDAO.listGames();
    }

    public JoinGameResult joinGame(String authToken, JoinGameRequest request) throws AuthenticationException, AlreadyTakenException {
        // Validate the authToken
        if (!authDAO.isValidToken(authToken)) {
            throw new AuthenticationException("Invalid or expired authToken.");
        }

        // Check if the color is already taken
        boolean colorTaken = gameDAO.isColorTaken(request.gameID(), request.color());
        if (colorTaken) {
            throw new AlreadyTakenException("Color already taken.");
        }

        // Proceed to attempt to join the game
        boolean joined = gameDAO.joinGame(request.gameID(), request.color(), authToken, authDAO.getUsernameFromToken(authToken));
        if (joined) {
            return new JoinGameResult(true, "Successfully joined the game.");
        } else {
            // Handle other failure reasons
            return new JoinGameResult(false, "Failed to join the game. It may be full, or the game ID may be incorrect.");
        }
    }

}
