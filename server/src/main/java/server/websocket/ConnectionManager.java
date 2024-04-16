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
        System.out.println("Session added for game ID " + gameId);
    }

    public void removeSession(int gameId, Session session) {
        Set<Session> sessions = gameSessions.get(gameId);
        if (sessions != null) {
            sessions.remove(session);
            System.out.println("Session removed for game ID " + gameId);
            if (sessions.isEmpty()) {
                gameSessions.remove(gameId);
                System.out.println("No more sessions for game ID " + gameId + ", removing from manager");
            }
        }
    }

    public void sendMessageToSession(Session session, String message) {
        try {
            if (session != null && session.isOpen()) {
                session.getRemote().sendString(message);
            } else {
                System.out.println("Session is closed or null, cannot send message.");
            }
        } catch (IOException e) {
            System.out.println("Failed to send message to session: " + e.getMessage());
        }
    }

    public void broadcastToGame(int gameId, String message) {
        Set<Session> sessions = gameSessions.get(gameId);
        if (sessions != null) {
            for (Session session : sessions) {
                try {
                    if (session.isOpen()) {
                        session.getRemote().sendString(message);
                    }
                } catch (IOException e) {
                    System.out.println("Error sending message to one of the game " + gameId + " sessions: " + e.getMessage());
                }
            }
        } else {
            System.out.println("No sessions to broadcast for game ID " + gameId);
        }
    }

    public void broadcastToGameExcept(int gameId, Session exceptSession, String message) {
        Set<Session> sessions = gameSessions.get(gameId);
        if (sessions != null) {
            for (Session session : sessions) {
                try {
                    if (session != exceptSession && session.isOpen()) {
                        session.getRemote().sendString(message);
                    }
                } catch (IOException e) {
                    System.out.println("Error sending message to one of the game " + gameId + " sessions (excluding one session): " + e.getMessage());
                }
            }
        } else {
            System.out.println("No sessions to broadcast for game ID " + gameId);
        }
    }
}
