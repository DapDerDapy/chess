package server.websocket;

import org.eclipse.jetty.websocket.api.Session;
import java.io.IOException;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class ConnectionManager {
    private final Map<Integer, Set<Session>> gameSessions = new ConcurrentHashMap<>();

    public void addSession(int gameId, Session session) {
        gameSessions.computeIfAbsent(gameId, k -> ConcurrentHashMap.newKeySet()).add(session);
    }

    public void removeSession(int gameId, Session session) {
        Set<Session> sessions = gameSessions.get(gameId);
        if (sessions != null) {
            sessions.remove(session);
            if (sessions.isEmpty()) {
                gameSessions.remove(gameId);
            }
        }
    }

    public void sendMessageToSession(Session session, String message) throws IOException {
        if (session != null && session.isOpen()) {
            session.getRemote().sendString(message);  // Directly use session object
        } else {
            System.out.println("Session is closed or null.");
        }
    }

    public void broadcastToGame(int gameId, String message) throws IOException {
        Set<Session> sessions = gameSessions.get(gameId);
        for (Session session : sessions) {
            if (session.isOpen()) {
                session.getRemote().sendString(message);
            }
        }
    }

    public void broadcastToGameExcept(int gameId, Session exceptSession, String message) throws IOException {
        Set<Session> sessions = gameSessions.get(gameId);
        for (Session session : sessions) {
            if (session != exceptSession && session.isOpen()) {
                session.getRemote().sendString(message);
            }
        }
    }
}
