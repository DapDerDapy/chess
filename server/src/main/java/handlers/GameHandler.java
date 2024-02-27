package handlers;

import service.AdminService;
import service.GameService;
import spark.Request;
import spark.Response;
import com.google.gson.Gson;
import service.UserService;
import result.*;
import request.*;
import chess.ChessGame;
import exceptions.AuthenticationException;
import model.GameData;
import dataAccess.GameDAO;

import java.util.Collection;

public class GameHandler {

    private final Gson gson;

    private final GameService gameService;

    private final UserService userService;

    private final AdminService adminService;

    public GameHandler(GameService gameService, UserService userService, AdminService adminService){
        this.gameService = gameService;
        this.userService = userService;
        this.adminService = adminService;
        this.gson = new Gson();
    }

    public Object handleGameCreation(Request req, Response res) {
        try {
            String authToken = req.headers("Authorization");
            if (authToken == null || authToken.isEmpty()) {
                res.status(401); // Unauthorized
                return gson.toJson(new SimpleResponse(false, "error: No authorization token provided."));
            }

            if (!adminService.checkAuth(authToken)) {
                res.status(401); // Unauthorized
                return gson.toJson(new SimpleResponse(false, "error: Invalid or expired authorization token."));
            }

            GameCreationRequest gameCreationRequest = gson.fromJson(req.body(), GameCreationRequest.class);
            ChessGame chessGame = new ChessGame(); // Adjust based on your ChessGame class constructor

            GameCreationResult createdGame = gameService.createGame(authToken, gameCreationRequest.gameName(),
                    gameCreationRequest.blackUsername(), gameCreationRequest.whiteUsername(), chessGame);

            if (createdGame.success()) {
                res.status(200); // OK
                // Directly return the createdGame as it already contains the necessary information
                return gson.toJson(createdGame);
            } else {
                res.status(400); // Bad Request
                return gson.toJson(new SimpleResponse(false, "error: Failed to create game. Check request parameters."));
            }
        } catch (AuthenticationException e) {
            res.status(401); // Unauthorized
            return gson.toJson(new SimpleResponse(false, "Authentication error: " + e.getMessage()));
        } catch (IllegalArgumentException e) {
            res.status(400); // Bad Request
            return gson.toJson(new SimpleResponse(false, "Invalid parameters: " + e.getMessage()));
        } catch (Exception e) {
            res.status(500); // Internal Server Error
            return gson.toJson(new SimpleResponse(false, "An error occurred while creating the game: " + e.getMessage()));
        }
    }

    public Object listGames(Request req, Response res) {
        try {
            String authToken = req.headers("Authorization");
            if (authToken == null || authToken.isEmpty()) {
                res.status(401); // Unauthorized
                return gson.toJson(new SimpleResponse(false, "error: No authorization token provided."));
            }

            // Assuming you have an adminService or userService to check the auth token
            if (!adminService.checkAuth(authToken)) {
                res.status(401); // Unauthorized
                return gson.toJson(new SimpleResponse(false, "error: Invalid or expired authorization token."));
            }

            // Fetch the list of games
            Collection<GameData> games = gameService.listGames(authToken);
            if (games.isEmpty()) {
                res.status(200); // OK, but no games available
                return gson.toJson(new SimpleResponse(true, "No games available."));
            }

            res.status(200); // OK
            return gson.toJson(games); // Directly serialize the list of games to JSON
        } catch (Exception e) {
            res.status(500); // Internal Server Error
            return gson.toJson(new SimpleResponse(false, "An error occurred while listing the games: " + e.getMessage()));
        }
    }

}
