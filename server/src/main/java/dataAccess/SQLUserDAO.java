package dataAccess;

import model.UserData;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class SQLUserDAO implements UserDAO {

    public SQLUserDAO() throws DataAccessException {
        DatabaseManager.setupDatabaseTables();
    }

    @Override
    public void clear() {
        String sql = "DELETE FROM users";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.executeUpdate();
        } catch (SQLException | DataAccessException e) {
            e.printStackTrace(); // Handle exceptions more gracefully in real applications
        }
    }

    @Override
    public void addUser(UserData user) {
        String sql = "INSERT INTO users (username, password, email) VALUES (?, ?, ?)";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, user.username());
            pstmt.setString(2, user.password());
            pstmt.setString(3, user.email());
            pstmt.executeUpdate();
        } catch (SQLException | DataAccessException e) {
            e.printStackTrace(); // Handle exceptions more gracefully in real applications
        }
    }

    @Override
    public boolean isEmpty() {
        String sql = "SELECT COUNT(*) AS count FROM users";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            if (rs.next()) {
                return rs.getInt("count") == 0;
            }
        } catch (SQLException | DataAccessException e) {
            e.printStackTrace(); // Handle exceptions more gracefully in real applications
        }
        return true; // Assume empty if an exception occurs
    }

    @Override
    public UserData getUser(String username) {
        String sql = "SELECT * FROM users WHERE username = ?";
        try (Connection conn = DatabaseManager.getConnection();
            PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, username);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return new UserData(
                            rs.getString("username"),
                            rs.getString("password"),
                            rs.getString("email")
                    );
                }
            }
        } catch (SQLException | DataAccessException e) {
            e.printStackTrace(); // Handle exceptions more gracefully in real applications
        }
        return null; // Return null if user not found or an exception occurs
    }
}
