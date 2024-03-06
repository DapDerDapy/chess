package dataAccessTests;

import dataAccess.SQLUserDAO;
import dataAccess.DatabaseManager;
import model.UserData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.AfterEach;

import javax.xml.crypto.Data;

import static org.junit.jupiter.api.Assertions.*;

public class SQLUserDAOTests {
    private SQLUserDAO sqlUserDAO;
    private DatabaseManager dbManager;

    @BeforeEach
    void setUp() throws Exception {
        DatabaseManager.createDatabase();
        sqlUserDAO = new SQLUserDAO();
    }

    @AfterEach
    void tearDown() throws Exception {
        DatabaseManager.clear();
        DatabaseManager.setupDatabaseTables();
    }

    @Test
    void testAddUserAndGetUser() throws Exception {
        // Arrange
        String username = "user1";
        String password = "pass1";
        String email = "email1@example.com";
        UserData newUser = new UserData(username, password, email);

        // Act
        sqlUserDAO.addUser(newUser);
        UserData retrievedUser = sqlUserDAO.getUser(username);

        // Assert
        assertNotNull(retrievedUser, "The retrieved user should not be null.");
        assertEquals(username, retrievedUser.username(), "The username should match.");
        assertEquals(password, retrievedUser.password(), "The password should match.");
        assertEquals(email, retrievedUser.email(), "The email should match.");
    }

    @Test
    void testClear() throws Exception {
        // Arrange - Add a user
        sqlUserDAO.addUser(new UserData("user1", "pass1", "email1@example.com"));

        // Act - Clear users and check if the DAO is empty
        sqlUserDAO.clear();
        UserData user = sqlUserDAO.getUser("user1");

        // Assert
        assertNull(user, "The DAO should be empty after calling clear");
    }
}
