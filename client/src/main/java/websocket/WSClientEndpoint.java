package websocket;

import javax.websocket.*;
import java.net.URI;

@ClientEndpoint
public class WSClientEndpoint {

    private Session userSession = null;
    private MessageHandler messageHandler;

    public WSClientEndpoint(URI endpointURI) {
        try {
            WebSocketContainer container = ContainerProvider.getWebSocketContainer();
            container.connectToServer(this, endpointURI);
        } catch (Exception e) {
            System.out.println("WebSocket Client Error: " + e.getMessage());
            throw new RuntimeException(e);
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
        if (this.messageHandler != null) {
            messageHandler.handleMessage(message);
        }
    }

    @OnError
    public void onError(Session session, Throwable throwable) {
        System.out.println("WebSocket Client Error: " + throwable.getMessage());
    }

    public void addMessageHandler(MessageHandler msgHandler) {
        this.messageHandler = msgHandler;
    }

    public void sendMessage(String message) {
        if (this.userSession != null && this.userSession.isOpen()) {
            this.userSession.getAsyncRemote().sendText(message);
        } else {
            System.out.println("WebSocket connection is not open.");
        }
    }

    public static interface MessageHandler {
        void handleMessage(String message);
    }
}