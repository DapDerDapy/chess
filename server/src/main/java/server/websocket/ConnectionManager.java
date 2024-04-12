package server.websocket;

import javax.websocket.Session;
import java.io.IOException;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class ConnectionManager {
    private final Map<String, Session> userSessions = new ConcurrentHashMap<>();
    private final Map<Integer, Set<Session>> gameSessions = new ConcurrentHashMap<>();

    /**
     * Adds a user session.
     * @param username The user identifier.
     * @param session The WebSocket session associated with the user.
     */
    public void addUserSession(String username, Session session) {
        userSessions.put(username, session);
    }

    /**
     * Removes a user session.
     * @param username The user identifier.
     */
    public void removeUserSession(String username) {
        Session session = userSessions.remove(username);
        if (session != null) {
            gameSessions.values().forEach(sessions -> sessions.remove(session));
        }
    }

    /**
     * Adds a session to a specific game.
     * @param gameId The game identifier.
     * @param session The WebSocket session to add.
     */
    public void addGameSession(int gameId, Session session) {
        gameSessions.computeIfAbsent(gameId, k -> ConcurrentHashMap.newKeySet()).add(session);
    }


    /**
     *
     *
     *
     */
    public void addObserverSession(int gameId, Session session){

    }


    /**
     * Removes a session from a specific game.
     * @param gameId The game identifier.
     * @param session The WebSocket session to remove.
     */
    public void removeGameSession(int gameId, Session session) {
        Set<Session> sessions = gameSessions.get(gameId);
        if (sessions != null) {
            sessions.remove(session);
            if (sessions.isEmpty()) {
                gameSessions.remove(gameId);
            }
        }
    }

    /**
     * Sends a message to a specific user.
     * @param username The user identifier.
     * @param message The message to send.
     */
    public void sendMessageToUser(String username, String message) throws IOException {
        Session session = userSessions.get(username);
        if (session != null && session.isOpen()) {
            session.getBasicRemote().sendText(message);
        }
    }

    /**
     * Broadcasts a message to all users in a specific game.
     * @param gameId The game identifier.
     * @param message The message to broadcast.
     */
    public void broadcastToGame(int gameId, String message) throws IOException {
        Set<Session> sessions = gameSessions.get(gameId);
        for (Session session : sessions) {
            if (session.isOpen()) {
                session.getBasicRemote().sendText(message);
            }
        }
    }
}
