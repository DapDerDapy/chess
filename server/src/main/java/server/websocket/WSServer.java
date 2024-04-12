package server.websocket;
import server.Server;

import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.annotations.WebSocket;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketConnect;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketClose;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketMessage;


@WebSocket
public class WSServer {

    public static void startWebSocketServer() {
        Server server = new Server();

        try {
            server.run(8025);
            System.out.println("WebSocket server started.");
            // Keep server running
            Thread.sleep(Long.MAX_VALUE);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            server.stop();
        }
    }

    @OnWebSocketConnect
    public void onOpen(Session session) {
        System.out.println("Connection opened: " + session.getRemoteAddress().getAddress());
    }

    @OnWebSocketMessage
    public void onMessage(String message, Session session) {
        System.out.println("Message received from " + session.getRemoteAddress().getAddress() + ": " + message);
    }

    @OnWebSocketClose
    public void onClose(Session session, int statusCode, String reason) {
        System.out.println("Connection closed: " + session.getRemoteAddress().getAddress() + ", Code: " + statusCode + " Reason: " + reason);
    }

}
