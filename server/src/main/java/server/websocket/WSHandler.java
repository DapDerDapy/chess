package server.websocket;

import com.google.gson.Gson;
import org.eclipse.jetty.websocket.api.annotations.WebSocket;
import webSocketMessages.serverMessages.ServerMessage;
import webSocketMessages.userCommands.JoinObserver;
import webSocketMessages.userCommands.JoinPlayer;
import webSocketMessages.userCommands.UserGameCommand;

import javax.websocket.OnClose;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;

@WebSocket
public class WSHandler {

    private ConnectionManager connectionManager = new ConnectionManager();

    @OnOpen
    public void onOpen(Session session) {
        System.out.println("Session opened, id: " + session.getId());
        // You might want to associate this session with a specific user
    }

    @OnMessage
    public void onMessage(String message, Session session) {
        Gson gson = null;
        UserGameCommand command = gson.fromJson(message, UserGameCommand.class);

        switch (command.getCommandType()) {
            case JOIN_PLAYER:
                if (command instanceof JoinPlayer) {
                    handleJoinPlayer((JoinPlayer) command, session);
                }
                break;
            case JOIN_OBSERVER:
                if (command instanceof JoinObserver){
                    handleJoinObserver((JoinObserver) command, session);
                }
                break;
            // Handle other cases
        }
    }

    @OnClose
    public void onClose(Session session) {
        System.out.println("Session closed, id: " + session.getId());
        // Perform cleanup
    }

    private void handleJoinPlayer(JoinPlayer command, Session session) {
        try {
                Gson gson = null;
                connectionManager.addGameSession(command.getGameID(), session);
                ServerMessage loadGameMessage = new ServerMessage(ServerMessage.ServerMessageType.LOAD_GAME);
                connectionManager.sendMessageToUser(session.getId(), gson.toJson(loadGameMessage));

                ServerMessage notification = new ServerMessage(ServerMessage.ServerMessageType.NOTIFICATION);
                connectionManager.broadcastToGame(command.getGameID(), gson.toJson(notification));

        } catch (IOException e) {
            sendError(session, "Error: Failed to send message: " + e.getMessage());
        }
    }

    private void handleJoinObserver(JoinObserver command, Session session) {
        try {
            connectionManager.addObserverSession(command.getGameID(), session);
            ServerMessage loadGameMessage = new ServerMessage(ServerMessage.ServerMessageType.LOAD_GAME);
            connectionManager.sendMessageToUser(session.getId(), gson.toJson(loadGameMessage));

            ServerMessage notification = new ServerMessage(ServerMessage.ServerMessageType.NOTIFICATION);
            connectionManager.broadcastToObservers(command.getGameID(), gson.toJson(notification));

        } catch (IOException e) {
            sendError(session, "Error: Failed to join as observer: " + e.getMessage());
        }
    }

    private void sendError(Session session, String errorMsg) {
        try {
            Gson gson = null;
            Error error = new Error(errorMsg);
            connectionManager.sendMessageToUser(session.getId(), gson.toJson(error));
        } catch (IOException e) {
            e.printStackTrace();  // Consider proper logging
        }
    }
}
