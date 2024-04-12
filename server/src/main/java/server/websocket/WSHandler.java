package server.websocket;

import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketClose;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketConnect;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketMessage;
import org.eclipse.jetty.websocket.api.annotations.WebSocket;
import com.google.gson.Gson;
import server.websocket.ConnectionManager;
import webSocketMessages.serverMessages.LoadGame;
import webSocketMessages.serverMessages.Notification;
import webSocketMessages.serverMessages.ServerMessage;
import webSocketMessages.userCommands.JoinObserver;
import webSocketMessages.userCommands.JoinPlayer;
import webSocketMessages.userCommands.Leave;
import webSocketMessages.userCommands.UserGameCommand;

import java.io.IOException;



@WebSocket
public class WSHandler {

    private final ConnectionManager connectionManager = new ConnectionManager();
    private final Gson gson = new Gson();

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
                case JOIN_PLAYER:
                    handleJoinPlayer(gson.fromJson(message, JoinPlayer.class), session);
                    break;
                case JOIN_OBSERVER:
                    handleJoinObserver(gson.fromJson(message, JoinObserver.class), session);
                    break;
                case LEAVE:
                    handleLeaveGame(gson.fromJson(message, Leave.class), session);
                    break;
                default:
                    sendError(session, "Unsupported command type: " + command.getCommandType());
                    break;
            }
        } catch (Exception e) {
            System.err.println("Error processing message: " + message + "; Error: " + e.getMessage());
        }
    }


    private void handleJoinPlayer(JoinPlayer command, Session session) throws IOException {
        connectionManager.addSession(command.getGameID(), session);

        // Send load game message to the joining player
        String game = "Game in load game String!!";
        ServerMessage loadGameMessage = new LoadGame(ServerMessage.ServerMessageType.LOAD_GAME, game);

        connectionManager.sendMessageToSession(session, gson.toJson(loadGameMessage));

        // Notification to all other clients
        Notification notification = new Notification(ServerMessage.ServerMessageType.NOTIFICATION, "Player joined as : "+ command.getPlayerColor());
        connectionManager.broadcastToGameExcept(command.getGameID(), session, gson.toJson(notification));
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
            ServerMessage error = new ServerMessage(ServerMessage.ServerMessageType.ERROR);
            connectionManager.sendMessageToSession(session, gson.toJson(error));
        } catch (IOException e) {
            System.err.println("Failed to send error message: " + e.getMessage());
        }
    }


}
