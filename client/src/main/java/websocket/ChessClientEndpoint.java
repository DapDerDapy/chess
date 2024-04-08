package websocket;

import javax.websocket.ClientEndpoint;
import javax.websocket.OnMessage;
import javax.websocket.Session;

@ClientEndpoint
public class ChessClientEndpoint {

    @OnMessage
    public void onMessage(String message, Session session) {
        // Handle incoming messages from server
        System.out.println("Received message: " + message);
    }

    // Add methods for sending messages to the server if needed
}

