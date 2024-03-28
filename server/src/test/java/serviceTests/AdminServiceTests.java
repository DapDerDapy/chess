package serviceTests;

import dataAccess.MemoryAuthDAO;
import dataAccess.MemoryGameDAO;
import dataAccess.MemoryUserDAO;
import model.UserData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import service.AdminService;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class AdminServiceTests {

    private AdminService adminService;
    private MemoryUserDAO userDAO;
    private MemoryAuthDAO authDAO;
    private MemoryGameDAO gameDAO;

    @BeforeEach
    void setUp() {
        userDAO = new MemoryUserDAO();
        authDAO = new MemoryAuthDAO();
        gameDAO = new MemoryGameDAO();
        adminService = new AdminService(userDAO, authDAO, gameDAO);
    }

    @Test
    void clearApplicationData_ShouldClearAllData() {
        // Setup - Prepopulate some data
        userDAO.addUser(new UserData("user1", "password1", "email1"));
        String authToken = authDAO.createAuth("user1");
        gameDAO.createGame("ChessGame1", "user1", "user2", null); // Assuming ChessGame can be null for this example

        // Verify setup
        assertFalse(userDAO.isEmpty());
        assertTrue(authDAO.isValidToken(authToken));
        assertFalse(gameDAO.listGames().isEmpty());

        // Action - Call adminService.clearApplicationData()
        adminService.clearApplicationData();

        // Assert - Verify all data has been cleared
        assertTrue(userDAO.isEmpty());
        assertFalse(authDAO.isValidToken(authToken)); // Token should no longer be valid
        assertTrue(gameDAO.listGames().isEmpty());
    }

    @Test
    void checkAuth_ValidToken_ReturnsTrue() {
        // Setup
        String authToken = authDAO.createAuth("user1");

        // Action & Assert
        assertTrue(adminService.checkAuth(authToken));
    }

    @Test
    void checkAuth_InvalidToken_ReturnsFalse() {
        // Setup - Ensuring no auth tokens are created

        // Action & Assert
        assertFalse(adminService.checkAuth("invalidToken"));
    }

    // Add more tests as needed for other methods like getUsernameByToken
}
