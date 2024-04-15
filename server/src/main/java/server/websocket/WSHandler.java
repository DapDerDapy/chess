package server.websocket;

import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketClose;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketConnect;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketMessage;
import org.eclipse.jetty.websocket.api.annotations.WebSocket;
import com.google.gson.Gson;
import request.JoinGameRequest;
import result.JoinGameResult;
import server.websocket.ConnectionManager;
import service.GameService;
import service.UserService;
import webSocketMessages.serverMessages.LoadGame;
import webSocketMessages.serverMessages.Notification;
import webSocketMessages.serverMessages.ServerMessage;
import webSocketMessages.serverMessages.Error;
import webSocketMessages.userCommands.*;

import java.io.IOException;



@WebSocket
public class WSHandler {

    private final ConnectionManager connectionManager = new ConnectionManager();
    private final Gson gson = new Gson();

    private final GameService gameService;
    private final UserService userService;

    public WSHandler(GameService gameService, UserService userService) {
        this.gameService = gameService;
        this.userService = userService;
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
                //case MAKE_MOVE:
                //    handleMakeMove(gson.fromJson(message, MakeMove.class), session);
                //    break;
                default:
                    sendError(session, "Unsupported command type: " + command.getCommandType());
                    break;
            }
        } catch (Exception e) {
            System.err.println("Error processing message: " + message + "; Error: " + e.getMessage());
            sendError(session, e.getMessage());
        }
    }

    private void handleMakeMove(MakeMove command, Session session) throws IOException {
        connectionManager.addSession(command.getGameID(), session);
        String game = "Detailed game state here"; // Fetch actual game state
        ServerMessage loadGameMessage = new LoadGame(ServerMessage.ServerMessageType.LOAD_GAME, game);
        connectionManager.sendMessageToSession(session, gson.toJson(loadGameMessage));
    }


    private void handleJoinPlayer(JoinPlayer command, Session session) throws IOException {

        try {

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
        connectionManager.addSession(command.getGameID(), session);

        // Load game message to the observer
        String game = "Game in load game String!!";
        ServerMessage loadGameMessage = new LoadGame(ServerMessage.ServerMessageType.LOAD_GAME, game);
        connectionManager.sendMessageToSession(session, gson.toJson(loadGameMessage));

        // Notification to all other clients
        Notification notification = new Notification(ServerMessage.ServerMessageType.NOTIFICATION,"Observer joined the game.");
        connectionManager.broadcastToGameExcept(command.getGameID(), session, gson.toJson(notification));
    }

    private void handleLeaveGame(Leave command, Session session) throws IOException {
        connectionManager.removeSession(command.getGameID(), session);
        Notification notification = new Notification(ServerMessage.ServerMessageType.NOTIFICATION,"Player has left the game.");
        connectionManager.broadcastToGame(command.getGameID(), gson.toJson(notification));
    }

    private void sendError(Session session, String errorMessage) {
        try {
            System.err.println("Sending error: " + errorMessage);  // Log the specific error message
            ServerMessage error = new Error(ServerMessage.ServerMessageType.ERROR, "Error!");
            connectionManager.sendMessageToSession(session, gson.toJson(error));
        } catch (IOException e) {
            System.err.println("Failed to send error message: " + e.getMessage());
        }
    }


}
