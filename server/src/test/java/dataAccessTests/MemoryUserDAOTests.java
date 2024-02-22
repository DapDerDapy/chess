package dataAccessTests;

import model.UserData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import dataAccess.MemoryUserDAO;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

public class MemoryUserDAOTests {

    private MemoryUserDAO memoryUserDAO;

    @BeforeEach
    void setUp() {
        memoryUserDAO = new MemoryUserDAO();
    }

    /**
     * Add and Clear
     */
    @Test
    void testClear() {
        // Add some dummy users to the DAO
        memoryUserDAO.addUser(new UserData("user1", "pass1", "email1@example.com"));
        memoryUserDAO.addUser(new UserData("user2", "pass2", "email2@example.com"));

        // Clear all users
        memoryUserDAO.clear();

        // Verify the DAO is empty
        assertTrue(memoryUserDAO.isEmpty(), "The DAO should be empty after calling clear");
    }

    @Test
    void testGetUser() {
        // Given
        String username = "user1";
        String password = "pass1";
        String email = "email1@example.com";
        UserData expectedUser = new UserData(username, password, email);
        memoryUserDAO.addUser(expectedUser);

        // When
        UserData retrievedUser = memoryUserDAO.getUser(username);

        // Then
        assertEquals(expectedUser, retrievedUser, "Retrieved user should match the expected user");

        // Additionally, test retrieving a non-existing user
        UserData nonExistingUser = memoryUserDAO.getUser("nonExistingUser");
        assertNull(nonExistingUser, "Should return null for non-existing user");
    }



}