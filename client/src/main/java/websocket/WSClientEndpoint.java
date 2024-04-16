package websocket;

import javax.websocket.*;
import java.io.IOException;
import java.net.URI;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;

import chess.ChessGame;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

@ClientEndpoint
public class WSClientEndpoint {
    private volatile Session userSession = null;
    private final URI endpointURI;
    private final Consumer<ChessGame> gameUpdateHandler;
    private final WebSocketContainer container;
    private static final int MAX_RECONNECT_ATTEMPTS = 3;
    private AtomicInteger reconnectAttempts = new AtomicInteger(0);

    public WSClientEndpoint(URI endpointURI, Consumer<ChessGame> gameUpdateHandler) {
        this.endpointURI = endpointURI;
        this.gameUpdateHandler = gameUpdateHandler;
        this.container = ContainerProvider.getWebSocketContainer();
        connect();  // Automatically try to connect upon instantiation
    }

    public synchronized void connect() {
        while (!isConnected() && reconnectAttempts.getAndIncrement() < MAX_RECONNECT_ATTEMPTS) {
            try {
                this.container.connectToServer(this, endpointURI);
                break;  // Exit loop if connection is successful
            } catch (Exception e) {
                System.out.println("WebSocket Client Error on connection: " + e.getMessage() + ". Attempt " + reconnectAttempts);
                try {
                    Thread.sleep(1000 * reconnectAttempts.get());  // Exponential backoff
                } catch (InterruptedException ie) {
                    Thread.currentThread().interrupt();
                    System.out.println("Reconnection attempt interrupted.");
                }
            }
        }
        if (isConnected()) {
            reconnectAttempts.set(0);  // Reset reconnect attempts after successful connection
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
        connect();  // Try to reconnect automatically when the session is closed
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
        if (session.isOpen()) {
            try {
                session.close(new CloseReason(CloseReason.CloseCodes.UNEXPECTED_CONDITION, "Error occurred"));
            } catch (IOException e) {
                System.out.println("Error closing session after an error: " + e.getMessage());
            }
        }
        connect();  // Try to reconnect if an error occurs
    }

    public void sendMessage(String message) {
        if (isConnected()) {
            this.userSession.getAsyncRemote().sendText(message);
        } else {
            System.out.println("WebSocket connection is not open. Attempting to reconnect...");
            connect();
            if (isConnected()) {
                this.userSession.getAsyncRemote().sendText(message);
            } else {
                System.out.println("Reconnection failed. Message not sent: " + message);
            }
        }
    }

    public boolean isConnected() {
        return this.userSession != null && this.userSession.isOpen();
    }

}
