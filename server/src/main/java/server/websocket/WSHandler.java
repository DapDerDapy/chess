package server.websocket;

import com.google.gson.Gson;
import org.eclipse.jetty.websocket.api.annotations.WebSocket;
import webSocketMessages.serverMessages.ServerMessage;
import webSocketMessages.userCommands.JoinObserver;
import webSocketMessages.userCommands.JoinPlayer;
import webSocketMessages.userCommands.Leave;
import webSocketMessages.userCommands.UserGameCommand;

import javax.websocket.OnClose;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;

//@ServerEndpoint("/connect")
@WebSocket
public class WSHandler {

    private ConnectionManager connectionManager = new ConnectionManager();
    private Gson gson = null;

    @OnOpen
    public void onOpen(Session session) {
        System.out.println("Session opened, id: " + session.getId());
        // You might want to associate this session with a specific user
    }

    @OnMessage
    public void onMessage(String message, Session session) {
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
            case LEAVE:
                if (command instanceof Leave){
                    handleLeaveGame((Leave) command, session);
                }
            // Handle other cases
        }
    }

    @OnClose
    public void onClose(Session session) {
        System.out.println("Session closed, id: " + session.getId());
        // Perform cleanup
    }

    private void processGameEntry(int gameId, Session session) {
        try {
            connectionManager.addSession(gameId, session);
            ServerMessage loadGameMessage = new ServerMessage(ServerMessage.ServerMessageType.LOAD_GAME);
            connectionManager.sendMessageToSession(session.getId(), gson.toJson(loadGameMessage));

            ServerMessage notification = new ServerMessage(ServerMessage.ServerMessageType.NOTIFICATION);
            connectionManager.broadcastToGame(gameId, gson.toJson(notification));
        } catch (IOException e) {
            sendError(session, "Error: Failed to process game entry: " + e.getMessage());
        }
    }

    private void handleJoinPlayer(JoinPlayer command, Session session) {
        processGameEntry(command.getGameID(), session);
    }

    private void handleJoinObserver(JoinObserver command, Session session) {
        processGameEntry(command.getGameID(), session);
    }

    private void sendError(Session session, String errorMsg) {
        try {
            Error error = new Error(errorMsg);
            connectionManager.sendMessageToSession(session.getId(), gson.toJson(error));
        } catch (IOException e) {
            e.printStackTrace();  // To check for something at least
        }
    }

    private void handleLeaveGame(Leave command, Session session) {
        connectionManager.removeSession(command.getGameID(), session); // Assumes removeSession handles all types
    }



}
