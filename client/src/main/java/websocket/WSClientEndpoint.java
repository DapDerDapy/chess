package websocket;

import javax.websocket.*;
import java.net.URI;
import java.util.function.Consumer;

import chess.ChessGame;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

@ClientEndpoint
public class WSClientEndpoint {

    private Session userSession = null;
    private final URI endpointURI;
    private final Consumer<ChessGame> gameUpdateHandler;
    private final WebSocketContainer container;

    public WSClientEndpoint(URI endpointURI, Consumer<ChessGame> gameUpdateHandler) {
        this.endpointURI = endpointURI;
        this.gameUpdateHandler = gameUpdateHandler;
        this.container = ContainerProvider.getWebSocketContainer();
    }

    public void connect() {
        try {
            this.container.connectToServer(this, endpointURI);
        } catch (Exception e) {
            System.out.println("WebSocket Client Error: " + e.getMessage());
            throw new RuntimeException("Failed to connect to WebSocket server at " + endpointURI, e);
        }
    }

    @OnOpen
    public void onOpen(Session session) {
        System.out.println("Opening WebSocket client session.");
        this.userSession = session;
    }

    @OnClose
    public void onClose(Session session, CloseReason closeReason) {
        System.out.println("Closing WebSocket client session. Reason: " + closeReason);
        this.userSession = null;
    }

    @OnMessage
    public void onMessage(String message) {
        try {
            ChessGame game = new Gson().fromJson(message, ChessGame.class);
            if (game != null) {
                gameUpdateHandler.accept(game);
            }
        } catch (JsonSyntaxException e) {
            System.out.println("Failed to parse incoming message as ChessGame: " + e.getMessage());
        }
    }

    @OnError
    public void onError(Session session, Throwable throwable) {
        System.out.println("WebSocket Client Error: " + throwable.getMessage());
    }

    public void sendMessage(String message) {
        if (this.userSession != null && this.userSession.isOpen()) {
            this.userSession.getAsyncRemote().sendText(message);
        } else {
            System.out.println("WebSocket connection is not open. Attempting to reconnect...");
            connect();  // Attempt to reconnect
            if (this.userSession != null && this.userSession.isOpen()) {
                this.userSession.getAsyncRemote().sendText(message);
            } else {
                System.out.println("Reconnection failed. Message not sent: " + message);
            }
        }
    }

    public Session getSession() {
        return userSession;
    }

    public boolean isConnected() {
        return this.userSession != null && this.userSession.isOpen();
    }
}
