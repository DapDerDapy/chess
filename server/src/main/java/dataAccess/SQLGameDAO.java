package dataAccess;

import chess.ChessGame;
import com.google.gson.Gson;
import model.GameData;
import request.JoinGameRequest;

import java.sql.*;
import java.util.ArrayList;
import java.util.Collection;

public class  SQLGameDAO implements GameDAO {
    private final Gson gson = new Gson();

    public SQLGameDAO() throws DataAccessException {
        DatabaseManager.setupDatabaseTables();
    }

    @Override
    public int createGame(String gameName, String blackUsername, String whiteUsername, ChessGame gameState) {
        String sql = "INSERT INTO games (game_name, black_username, white_username, game_state) VALUES (?, ?, ?, ?);";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setString(1, gameName);
            pstmt.setString(2, blackUsername);
            pstmt.setString(3, whiteUsername);
            pstmt.setString(4, gson.toJson(gameState)); // Serialize gameState to JSON

            int affectedRows = pstmt.executeUpdate();
            if (affectedRows == 0) {
                throw new SQLException("Creating game failed, no rows affected.");
            }

            try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    return generatedKeys.getInt(1);
                } else {
                    throw new SQLException("Creating game failed, no ID obtained.");
                }
            }
        } catch (SQLException | DataAccessException e) {
            throw new RuntimeException("Error creating a new game: " + e.getMessage());
        }
    }

    @Override
    public GameData getGame(int gameId) {
        String sql = "SELECT * FROM games WHERE game_id = ?;";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, gameId);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return new GameData(
                            rs.getInt("game_id"),
                            rs.getString("black_username"),
                            rs.getString("white_username"),
                            rs.getString("game_name"),
                            gson.fromJson(rs.getString("game_state"), ChessGame.class)); // Deserialize game state from JSON
                }
            }
        } catch (SQLException | DataAccessException e) {
            throw new RuntimeException("Error fetching game: " + e.getMessage());
        }
        return null;
    }

    @Override
    public Collection<GameData> listGames() {
        String sql = "SELECT * FROM games;";
        ArrayList<GameData> gamesList = new ArrayList<>();
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            while (rs.next()) {
                gamesList.add(new GameData(
                        rs.getInt("game_id"),
                        rs.getString("white_username"),
                        rs.getString("black_username"),
                        rs.getString("game_name"),
                        gson.fromJson(rs.getString("game_state"), ChessGame.class))); // Deserialize game state from JSON
            }
        } catch (SQLException | DataAccessException e) {
            throw new RuntimeException("Error listing games: " + e.getMessage());
        }
        return gamesList;
    }

    @Override
    public boolean joinGame(int gameID, String color, String authToken, String username) {
        Connection conn = null;
        try {
            conn = DatabaseManager.getConnection();
            conn.setAutoCommit(false); // Disable auto-commit to manage transaction manually

            if (color == null || color.isEmpty()) {
                // Joining as a watcher, assuming there's a watchers table or a way to mark them in game_participants
                String sqlInsertWatcher = "INSERT INTO game_watchers (game_id, username) VALUES (?, ?);";
                try (PreparedStatement pstmt = conn.prepareStatement(sqlInsertWatcher)) {
                    pstmt.setInt(1, gameID);
                    pstmt.setString(2, username);
                    pstmt.executeUpdate();
                }
            } else {
                // Joining as a player, make sure color is not already taken
                if (isColorTaken(gameID, color)) {
                    conn.rollback(); // Rollback to ensure consistency
                    return false;
                }

                String updateSql = "UPDATE games SET ";
                if ("BLACK".equalsIgnoreCase(color)) {
                    updateSql += "black_username = ? ";
                } else if ("WHITE".equalsIgnoreCase(color)) {
                    updateSql += "white_username = ? ";
                }
                updateSql += "WHERE game_id = ?;";

                try (PreparedStatement pstmtUpdate = conn.prepareStatement(updateSql)) {
                    pstmtUpdate.setString(1, username);
                    pstmtUpdate.setInt(2, gameID);
                    int rowsUpdated = pstmtUpdate.executeUpdate();
                    if (rowsUpdated == 0) {
                        conn.rollback(); // Rollback in case no rows were updated, which means game ID does not exist
                        return false;
                    }
                }
            }

            conn.commit(); // Commit the transaction if everything went fine
            return true;
        } catch (SQLException | DataAccessException e) {
            e.printStackTrace(); // Consider proper logging
            // Handle rollback in catch block if necessary
            try {
                if (conn != null) conn.rollback();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
            throw new RuntimeException("Error joining game: " + e.getMessage());
        } finally {
            // Finally block for setting auto-commit true and closing the connection is unnecessary with try-with-resources
        }
    }






    @Override
    public boolean isColorTaken(int gameID, String color) {
        String sqlCheck;
        if ("BLACK".equalsIgnoreCase(color)) {
            sqlCheck = "SELECT count(*) FROM games WHERE game_id = ? AND black_username IS NOT NULL;";
        } else if ("WHITE".equalsIgnoreCase(color)) {
            sqlCheck = "SELECT count(*) FROM games WHERE game_id = ? AND white_username IS NOT NULL;";
        } else {
            // If color is neither BLACK nor WHITE, assume it's not taken
            return false;
        }

        try (Connection conn = DatabaseManager.getConnection();
            PreparedStatement pstmt = conn.prepareStatement(sqlCheck)) {
            pstmt.setInt(1, gameID);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    int count = rs.getInt(1);
                    return count > 0; // If count > 0, then the specified color is already taken
                }
            }
        } catch (SQLException | DataAccessException e) {
            throw new RuntimeException("Error checking if color is taken: " + e.getMessage());
        }
        return false; // Default to false if not found
    }




    @Override
    public void clearAll() {
        // Start with deleting data from child tables to maintain referential integrity
        String sqlDeleteParticipants = "DELETE FROM game_participants;";
        String sqlDeleteGames = "DELETE FROM games;";

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmtParticipants = conn.prepareStatement(sqlDeleteParticipants);
             PreparedStatement pstmtGames = conn.prepareStatement(sqlDeleteGames)) {

            // Delete participants first to avoid foreign key constraint violations
            pstmtParticipants.executeUpdate();

            // Now, it's safe to delete the games
            pstmtGames.executeUpdate();
        } catch (SQLException | DataAccessException e) {
            throw new RuntimeException("Error clearing games data: " + e.getMessage());
        }
    }

    @Override
    public boolean updateGame(int gameID, ChessGame updatedChessGame) {
        String sql = "UPDATE games SET game_state = ? WHERE game_id = ?;";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, gson.toJson(updatedChessGame)); // Serialize the updated game state to JSON
            pstmt.setInt(2, gameID);

            int affectedRows = pstmt.executeUpdate();
            return affectedRows > 0; // If affectedRows is greater than 0, the update was successful
        } catch (SQLException | DataAccessException e) {
            throw new RuntimeException("Error updating game: " + e.getMessage());
        }
    }

    @Override
    public boolean rejoinPlayer(int gameID, String color, String username) {
        String sqlCheck = "";
        if ("BLACK".equalsIgnoreCase(color)) {
            sqlCheck = "SELECT count(*) FROM games WHERE game_id = ? AND black_username = ?;";
        } else if ("WHITE".equalsIgnoreCase(color)) {
            sqlCheck = "SELECT count(*) FROM games WHERE game_id = ? AND white_username = ?;";
        } else {
            // If color is neither BLACK nor WHITE, it's an invalid request
            return false;
        }

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sqlCheck)) {
            pstmt.setInt(1, gameID);
            pstmt.setString(2, username);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    int count = rs.getInt(1);
                    return count > 0; // If count > 0, then the specified color is already taken by this username
                }
            }
        } catch (SQLException | DataAccessException e) {
            throw new RuntimeException("Error checking if player can rejoin: " + e.getMessage());
        }
        return false; // Default to false if not found
    }

    @Override
    public boolean joinObserverChecks(int gameId, String authToken) {
        // This SQL checks if the game exists and if the auth token is valid
        String sql = """
        SELECT EXISTS(
            SELECT 1 
            FROM games 
            WHERE game_id = ?
        ) AS game_exists,
        EXISTS(
            SELECT 1 
            FROM auth_tokens 
            WHERE auth_token = ?
        ) AS token_valid;
        """;

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, gameId);
            pstmt.setString(2, authToken);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    boolean gameExists = rs.getBoolean("game_exists");
                    boolean tokenIsValid = rs.getBoolean("token_valid");
                    return gameExists && tokenIsValid; // Only return true if both are true
                }
            }
        } catch (SQLException | DataAccessException e) {
            throw new RuntimeException("Error verifying game existence and token validity: " + e.getMessage());
        }
        return false; // Default to false if query fails or no results found
    }




}
