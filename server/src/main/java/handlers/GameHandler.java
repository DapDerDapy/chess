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

            // Properly instantiate ChessGame
            // This example assumes a default constructor sets up a new game.
            ChessGame chessGame = new ChessGame(); // Ensure this matches your ChessGame class' constructor

            // Now passing authToken as the first parameter
            GameData createdGame = gameService.createGame(authToken, gameCreationRequest.gameName(),
                    gameCreationRequest.blackUsername(), gameCreationRequest.whiteUsername(), chessGame);

            if (createdGame != null) {
                res.status(200); // OK
                return gson.toJson(new GameCreationResult(true, "Game created successfully", createdGame.id()));
            } else {
                // Assuming failure due to invalid request parameters, as authentication has already been checked.
                res.status(400); // Bad Request
                return gson.toJson(new SimpleResponse(false, "error: Failed to create game. Check request parameters."));
            }
        } catch (Exception e) {
            res.status(500); // Internal Server Error
            return gson.toJson(new SimpleResponse(false, "An error occurred while creating the game."));
        }
    }

}
