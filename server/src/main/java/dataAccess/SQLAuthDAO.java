package dataAccess;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

public class SQLAuthDAO implements AuthDAO {


    @Override
    public String createAuth(String username) {
        String authToken = UUID.randomUUID().toString();
        String sql = "INSERT INTO auth_tokens (auth_token, username) VALUES (?, ?);";

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, authToken);
            stmt.setString(2, username);
            stmt.executeUpdate();

            return authToken;
        } catch (SQLException | DataAccessException) {
            throw new DataAccessException("Failed to create auth token: " + e.getMessage());
        }
    }

    @Override
    public String getAuth(String authToken) {
        String sql = "SELECT username FROM auth_tokens WHERE token = ?";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, authToken);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getString("username");
                }
            }
        } catch (SQLException | DataAccessException e) {
            System.err.println("Failed to retrieve auth token: " + e.getMessage());
            // Handle exception or rethrow as appropriate
        }
        return null;
    }

    @Override
    public boolean deleteAuth(String authToken) {
        String sql = "DELETE FROM auth_tokens WHERE token = ?";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, authToken);
            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException | DataAccessException e) {
            System.err.println("Failed to delete auth token: " + e.getMessage());
            // Handle exception or rethrow as appropriate
        }
        return false;
    }

    @Override
    public boolean isValidToken(String authToken) {
        String sql = "SELECT COUNT(*) FROM auth_tokens WHERE token = ?";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, authToken);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        } catch (SQLException | DataAccessException e) {
            System.err.println("Failed to validate auth token: " + e.getMessage());
            // Handle exception or rethrow as appropriate
        }
        return false;
    }

    @Override
    public String getUsernameFromToken(String authToken) {
        // This method can be the same as getAuth() since it retrieves the username based on the token
        return getAuth(authToken);
    }

    @Override
    public void clearAll() {
        String sql = "DELETE FROM auth_tokens";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.executeUpdate();
        } catch (SQLException | DataAccessException e) {
            System.err.println("Failed to clear auth tokens: " + e.getMessage());
            // Handle exception or rethrow as appropriate
        }
    }
}
