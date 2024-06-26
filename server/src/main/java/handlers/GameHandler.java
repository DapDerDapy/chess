package handlers;

import chess.ChessGame;
import com.google.gson.Gson;
import exceptions.AlreadyTakenException;
import exceptions.AuthenticationException;
import exceptions.InvalidGameIdException;
import model.GameData;
import request.GameCreationRequest;
import request.JoinGameRequest;
import result.GameCreationResult;
import result.JoinGameResult;
import service.AdminService;
import service.GameService;
import service.UserService;
import spark.Request;
import spark.Response;
import wrappers.GamesWrapper;

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

            if (!adminService.checkAuth(authToken)) {
                res.status(401); // Unauthorized
                return gson.toJson(new SimpleResponse(false, "error: Invalid or expired authorization token."));
            }

            Collection<GameData> games = gameService.listGames(authToken);

            // Wrap the games list into the GamesWrapper, something is wrong with Gson conversion things?
            GamesWrapper wrapper = new GamesWrapper(games);
            // Serialize the wrapper to JSON, which will include the "games" root element
            String jsonOutput = gson.toJson(wrapper);

            res.status(200); // OK
            return jsonOutput; // Return the serialized JSON of the wrapped games
        } catch (Exception e) {
            res.status(500); // Internal Server Error
            return gson.toJson(new SimpleResponse(false, "An error occurred while listing the games: " + e.getMessage()));
        }
    }


    public Object joinGameHandler(Request req, Response res) {
        try {
            String authToken = req.headers("Authorization");
            if (authToken == null || authToken.isEmpty()) {
                res.status(401); // Unauthorized
                return gson.toJson(new SimpleResponse(false, "Error: No authorization token provided."));
            }

            JoinGameRequest joinRequest = gson.fromJson(req.body(), JoinGameRequest.class);
            JoinGameResult joinResult = gameService.joinGame(authToken, joinRequest);

            if (joinResult.success()) {
                res.status(200); // Success
                return gson.toJson(joinResult);
            } else {
                res.status(400); // Bad Request
                return gson.toJson(new SimpleResponse(false, joinResult.message()));
            }
        } catch (InvalidGameIdException e) {
            res.status(400); // Bad Request
            return gson.toJson(new SimpleResponse(false, e.getMessage()));
        } catch (AuthenticationException e) {
            res.status(401); // Unauthorized
            return gson.toJson(new SimpleResponse(false, "Error: unauthorized"));
        } catch (AlreadyTakenException e) {
            res.status(403); // Forbidden
            return gson.toJson(new SimpleResponse(false, e.getMessage()));
        } catch (Exception e) {
            res.status(500); // Internal Server Error
            return gson.toJson(new SimpleResponse(false, "Error: " + e.getMessage()));
        }
    }

}
