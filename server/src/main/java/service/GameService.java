package service;

import chess.ChessGame;
import dataAccess.AuthDAO;
import dataAccess.GameDAO;
import dataAccess.UserDAO;
import exceptions.AlreadyTakenException;
import exceptions.AuthenticationException;
import exceptions.InvalidGameIdException;
import model.GameData;
import request.JoinGameRequest;
import result.GameCreationResult;
import result.JoinGameResult;

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

    public JoinGameResult joinGame(String authToken, JoinGameRequest request) throws AuthenticationException, AlreadyTakenException, InvalidGameIdException {
        if (!authDAO.isValidToken(authToken)) {
            throw new AuthenticationException("Invalid or expired authToken.");
        }

        // Add a check for the validity of the game ID
        if (gameDAO.getGame(request.gameID()) == null) {
            throw new InvalidGameIdException("error: Invalid game ID: " + request.gameID());
        }

        // Check if the player is already in the game but has left
        if (checkColorTaken(authToken, request)){
            return new JoinGameResult(true, "Rejoined game!");
        }

        boolean colorTaken = gameDAO.isColorTaken(request.gameID(), request.playerColor());
        if (colorTaken) {
            throw new AlreadyTakenException("error: Color already taken.");
        }

        boolean joined = gameDAO.joinGame(request.gameID(), request.playerColor(), authToken, authDAO.getUsernameFromToken(authToken));
        if (joined) {
            return new JoinGameResult(true, "Successfully joined the game.");
        } else {
            return new JoinGameResult(false, "Failed to join the game. It may be full, or the game ID may be incorrect.");
        }
    }

    public boolean checkColorTaken(String authToken, JoinGameRequest request){
        return gameDAO.rejoinPlayer(request.gameID(), request.playerColor(), authDAO.getUsernameFromToken(authToken));
    }

    public boolean joinObserverChecks(int gameId, String authToken){
        return gameDAO.joinObserverChecks(gameId, authToken);
    }
    public boolean updateGameState(int gameID, ChessGame updatedChessGame) {
        return gameDAO.updateGame(gameID, updatedChessGame);
    }

    public void updateGameStatus(int gameId){
        gameDAO.updateGameStatus(gameId, "Resigned");
    }

    public String getGameStatus(int gameId){
        return gameDAO.getGameStatus(gameId);
    }

    public ChessGame.TeamColor getPlayerColor(int gameId, String username){
        return gameDAO.getPlayerColor(gameId, username);
    }

    public boolean setChessGame(int gameId, ChessGame game){
        return updateGameState(gameId, game);
    }

    public ChessGame getChessGame(int gameId){
        return gameDAO.getGameState(gameId);
    }


    public ChessGame getGame(int gameID) {
        try {
            GameData gameData = gameDAO.getGame(gameID);
            if (gameData == null) {
                return null;  // or throw an appropriate exception if needed
            }
            return gameData.getGame();
        } catch (Exception e) {
            // Log the exception or handle it as per your error handling policy
            throw new RuntimeException("Failed to retrieve game: " + e.getMessage(), e);
        }
    }



}
