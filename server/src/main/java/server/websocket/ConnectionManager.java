package server.websocket;

import com.google.gson.Gson;

import javax.websocket.Session;
import java.io.IOException;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import chess.ChessGame;

public class ConnectionManager {
    private final Map<String, Session> userSessions = new ConcurrentHashMap<>();
    private final Map<Integer, Set<Session>> gameSessions = new ConcurrentHashMap<>();

    Gson gson = new Gson();

    /**
     * Adds a session to a specific game, whether player or observer.
     * @param gameId The game identifier.
     * @param session The WebSocket session to add.
     */
    public void addSession(int gameId, Session session) {
        gameSessions.computeIfAbsent(gameId, k -> ConcurrentHashMap.newKeySet()).add(session);
    }

    /**
     * Removes a session from a specific game, whether player or observer.
     * @param gameId The game identifier.
     * @param session The WebSocket session to remove.
     */
    public void removeSession(int gameId, Session session) {
        Set<Session> sessions = gameSessions.get(gameId);
        if (sessions != null) {
            sessions.remove(session);
            if (sessions.isEmpty()) {
                gameSessions.remove(gameId);
            }
        }
    }

    /**
     * Sends a message to a specific user by their session identifier.
     * @param sessionId The session identifier.
     * @param message The message to send.
     */
    public void sendMessageToSession(String sessionId, String message) throws IOException {
        Session session = userSessions.get(sessionId);
        if (session != null && session.isOpen()) {
            session.getBasicRemote().sendText(message);
        } else {
            System.out.println("Session not found or is closed for ID: " + sessionId);
        }
    }

    /**
     * Broadcasts a message to all sessions associated with a specific game.
     * @param gameId The game identifier.
     * @param message The message to broadcast.
     */
    public void broadcastToGame(int gameId, String message) throws IOException {
        Set<Session> sessions = gameSessions.get(gameId);
        if (sessions != null) {
            for (Session session : sessions) {
                if (session.isOpen()) {
                    session.getBasicRemote().sendText(message);
                }
            }
        }
    }

    /**
     * Adds a user's session to the map using their username as a key.
     * This is useful for personalized messaging or session management.
     * @param username The username of the user.
     * @param session The session to map to the username.
     */
    public void addUserSession(String username, Session session) {
        userSessions.put(username, session);
    }

    /**
     * Removes a user's session using their username.
     * @param username The username whose session is to be removed.
     */
    public void removeUserSession(String username) {
        Session session = userSessions.remove(username);
        if (session != null) {
            // Ensure to clean up any game associations if needed
            gameSessions.values().forEach(sessions -> sessions.remove(session));
        }
    }

    public void broadcastGameState(int gameId, ChessGame game) {
        String gameStateJson = new Gson().toJson(game);
        Set<Session> sessions = gameSessions.get(gameId);
        if (sessions != null) {
            for (Session session : sessions) {
                if (session.isOpen()) {
                    try {
                        session.getBasicRemote().sendText(gameStateJson);
                    } catch (IOException e) {
                        System.err.println("Failed to send game state to session " + session.getId() + ": " + e.getMessage());
                    }
                }
            }
        }
    }


}