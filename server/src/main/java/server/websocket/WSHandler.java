package server.websocket;

import chess.ChessGame;
import chess.ChessPiece;
import chess.ChessPosition;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketClose;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketConnect;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketMessage;
import org.eclipse.jetty.websocket.api.annotations.WebSocket;
import com.google.gson.Gson;
import request.JoinGameRequest;
import result.JoinGameResult;
import server.websocket.ConnectionManager;
import service.AdminService;
import service.GameService;
import service.UserService;
import webSocketMessages.serverMessages.LoadGame;
import webSocketMessages.serverMessages.Notification;
import webSocketMessages.serverMessages.ServerMessage;
import webSocketMessages.serverMessages.Error;
import webSocketMessages.userCommands.*;

import java.io.IOException;
import java.util.Objects;


@WebSocket
public class WSHandler {

    private final ConnectionManager connectionManager = new ConnectionManager();
    private final Gson gson = new Gson();

    private final GameService gameService;
    private final UserService userService;

    private final AdminService adminService;

    public WSHandler(GameService gameService, UserService userService, AdminService adminService) {
        this.gameService = gameService;
        this.userService = userService;
        this.adminService = adminService;
    }

    @OnWebSocketConnect
    public void onOpen(Session session) {
        System.out.println("Session opened, id: " + session.hashCode());  // Example usage
    }

    @OnWebSocketClose
    public void onClose(Session session, int statusCode, String reason) {
        System.out.println("Closed:");
    }

    @OnWebSocketMessage
    public void onMessage(Session session, String message) {
        try {
            UserGameCommand command = gson.fromJson(message, UserGameCommand.class);
            System.out.println("Received command type: " + command.getCommandType());  // Log command type
            switch (command.getCommandType()) {
                case LEAVE:
                    handleLeaveGame(gson.fromJson(message, Leave.class), session);
                    break;
                case JOIN_OBSERVER:
                    handleJoinObserver(gson.fromJson(message, JoinObserver.class), session);
                    break;
                case JOIN_PLAYER:
                    handleJoinPlayer(gson.fromJson(message, JoinPlayer.class), session);
                    break;
                case MAKE_MOVE:
                    handleMakeMove(gson.fromJson(message, MakeMove.class), session);
                    break;
                case RESIGN:
                    handleResign(gson.fromJson(message, Resign.class), session);
                    break;
                default:
                    sendError(session, "Unsupported command type: " + command.getCommandType());
                    break;
            }
        } catch (Exception e) {
            System.err.println("Error processing message: " + message + "; Error: " + e.getMessage());
            sendError(session, e.getMessage());
        }
    }

    private void handleResign(Resign command, Session session) throws IOException {
        try {
            // Retrieve the current game state
            ChessGame currentGame = gameService.getGame(command.getGameID());
            if (currentGame == null) {
                sendError(session, "Game not found.");
                return;
            }

            // Observer tries to resign
            String username = adminService.getUsernameByToken(command.getAuthToken());
            ChessGame.TeamColor playerColor = gameService.getPlayerColor(command.getGameID(), username);
            if (playerColor == null){
                sendError(session, "Observer cannot resign!");
                return;
            }

            // Handle Double Resign!
            if (Objects.equals(gameService.getGameStatus(command.getGameID()), "Resigned")){
                sendError(session, "Can't resign the same game twice!");
                return;
            }

            // Resign the game
            gameService.updateGameStatus(command.getGameID());

            // Notify all clients that the game has ended due to resignation
            String notificationMessage = "Game has been resigned by " + adminService.getUsernameByToken(command.getAuthToken());
            Notification notification = new Notification(ServerMessage.ServerMessageType.NOTIFICATION, notificationMessage);
            connectionManager.broadcastToGame(command.getGameID(), gson.toJson(notification));

        } catch (Exception e) {
            sendError(session, "Error processing resignation: " + e.getMessage());
        }
    }


    private void handleMakeMove(MakeMove command, Session session) throws IOException {


        try {
            // Retrieve the current game state
            ChessGame currentGame = gameService.getGame(command.getGameID());
            System.out.println("currentGame.getTeamTurn().toString() " + currentGame.getTeamTurn().toString());

            if (Objects.equals(gameService.getGameStatus(command.getGameID()), "Resigned")){
                sendError(session, "Resigned game! Can't make moves!");
                return;
            }

            if (currentGame == null) {
                sendError(session, "Game not found.");
                return;
            }

            ChessPiece piece = currentGame.getBoard().getPiece(command.getMove().getStartPosition());

            // Verify if the move is valid
            if (!currentGame.isMoveValid(command.getMove(), currentGame.getBoard(), command.getMove().getStartPosition())) {
                sendError(session, "Invalid move.");
                return;
            }

            // Is your Turn / Your piece
            if (piece.getTeamColor() != currentGame.getTeamTurn()){
                sendError(session, "Not your piece or turn!");
                return;
            }

            System.out.println("adminService.getUsernameByToken(command.getAuthToken()) " + adminService.getUsernameByToken(command.getAuthToken()));

            String username = adminService.getUsernameByToken(command.getAuthToken());
            ChessGame.TeamColor playerColor = gameService.getPlayerColor(command.getGameID(), username);
            currentGame.setTeamTurn(playerColor);

            // Apply the move
            currentGame.makeMove(command.getMove());

            // Update the game state in the database
            boolean updateSuccess = gameService.updateGameState(command.getGameID(), currentGame);
            if (!updateSuccess) {
                sendError(session, "Failed to update game state.");
                return;
            }

            // Serialize the updated game state to JSON
            String updatedGameStateJson = gson.toJson(new LoadGame(ServerMessage.ServerMessageType.LOAD_GAME, gson.toJson(currentGame)));

            // Send LOAD_GAME message to all clients including the root client
            connectionManager.broadcastToGame(command.getGameID(), updatedGameStateJson);

            Notification notification = new Notification(ServerMessage.ServerMessageType.NOTIFICATION,"Move has been made!");
            connectionManager.broadcastToGameExcept(command.getGameID(), session, gson.toJson(notification));

        } catch (Exception e) {
            sendError(session, "Error processing move: " + e.getMessage());
        }
    }


    private void handleJoinPlayer(JoinPlayer command, Session session) throws IOException {

        try {

            JoinGameRequest request = new JoinGameRequest(command.getGameID(), command.getPlayerColor().toString());
            boolean isColorTaken = gameService.checkColorTaken(command.getAuthToken(), request);

            if (!isColorTaken){
                sendError(session, "Error: Color taken!");
                return;
            }

            // If successful, proceed to add session and send game state
            connectionManager.addSession(command.getGameID(), session);
            String game = "Detailed game state here"; // Fetch actual game state
            ServerMessage loadGameMessage = new LoadGame(ServerMessage.ServerMessageType.LOAD_GAME, game);
            connectionManager.sendMessageToSession(session, gson.toJson(loadGameMessage));

            Notification notification = new Notification(ServerMessage.ServerMessageType.NOTIFICATION, "Player joined as: " + command.getPlayerColor());
            connectionManager.broadcastToGameExcept(command.getGameID(), session, gson.toJson(notification));

        } catch (Exception e) {
            // Handle different types of exceptions if necessary
            sendError(session, "Join game failed: " + e.getMessage());
        }
    }


    private void handleJoinObserver(JoinObserver command, Session session) throws IOException {


        boolean canObserverJoin = gameService.joinObserverChecks(command.getGameID(), command.getAuthToken());

        if (!canObserverJoin){
            sendError(session, "Error: bad GameID or AuthToken");
            return;
        }

        connectionManager.addSession(command.getGameID(), session);

        // Load game message to the observer
        String game = "Game in load game String!!";
        ServerMessage loadGameMessage = new LoadGame(ServerMessage.ServerMessageType.LOAD_GAME, game);
        connectionManager.sendMessageToSession(session, gson.toJson(loadGameMessage));

        // Notification to all other clients
        Notification notification = new Notification(ServerMessage.ServerMessageType.NOTIFICATION,"Observer joined the game.");connectionManager.broadcastToGameExcept(command.getGameID(), session, gson.toJson(notification));
    }

    private void handleLeaveGame(Leave command, Session session) throws IOException {
        connectionManager.removeSession(command.getGameID(), session);
        Notification notification = new Notification(ServerMessage.ServerMessageType.NOTIFICATION,"Player has left the game.");
        connectionManager.broadcastToGame(command.getGameID(), gson.toJson(notification));
    }

    private void sendError(Session session, String errorMessage) {
        System.err.println("Sending error: " + errorMessage);  // Log the specific error message
        ServerMessage error = new Error(ServerMessage.ServerMessageType.ERROR, "Error!");
        connectionManager.sendMessageToSession(session, gson.toJson(error));
    }


}
