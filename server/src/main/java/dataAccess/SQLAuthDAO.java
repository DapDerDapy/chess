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

        } catch (SQLException | DataAccessException e) {
            // Handle the SQL exception
            // For example, log the error or convert it to a runtime exception
            throw new RuntimeException("Failed to create auth token: " + e.getMessage());
        }

        return authToken;
    }


    @Override
    public String getAuth(String authToken) {
        // Assuming the table is called auth_tokens and the columns are auth_token for the token
        // and username for the username
        String sql = "SELECT username FROM auth_tokens WHERE auth_token = ?;";

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, authToken);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    // Retrieve and return the username associated with the authToken
                    return rs.getString("username");
                }
            }

        } catch (SQLException | DataAccessException e) {
            // Handle the exception appropriately
            throw new RuntimeException("Failed to retrieve auth token: " + e.getMessage());
        }

        // Return null if no matching auth token is found
        return null;
    }


    @Override
    public boolean deleteAuth(String authToken) {
        String sql = "DELETE FROM auth_tokens WHERE auth_token = ?;";

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, authToken);
            int affectedRows = stmt.executeUpdate();

            // If affectedRows is 1, then the deletion was successful
            return affectedRows == 1;

        } catch (SQLException | DataAccessException e) {
            // Handle the exception appropriately
            throw new RuntimeException("Failed to delete auth token: " + e.getMessage());
        }
    }


    @Override
    public boolean isValidToken(String authToken) {
        String sql = "SELECT COUNT(*) AS count FROM auth_tokens WHERE auth_token = ?;";

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, authToken);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    // Check if count is greater than 0, indicating the token exists
                    return rs.getInt("count") > 0;
                }
            }

        } catch (SQLException | DataAccessException e) {
            // Handle the exception appropriately
            throw new RuntimeException("Failed to validate auth token: " + e.getMessage());
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
