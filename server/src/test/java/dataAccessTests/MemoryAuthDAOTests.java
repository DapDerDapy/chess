package dataAccessTests;

import dataAccess.MemoryAuthDAO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

public class MemoryAuthDAOTests {

    private MemoryAuthDAO memoryAuthDAO;

    @BeforeEach
    void setUp() {
        memoryAuthDAO = new MemoryAuthDAO();
    }

    @Test
    void testCreateAndGetAuth() {
        // Given: A username
        String username = "testUser";

        // When: Creating an authorization and receiving the generated authToken
        String authToken = memoryAuthDAO.createAuth(username);

        // Then: The authToken can be retrieved and is associated with the correct username
        assertEquals(username, memoryAuthDAO.getAuth(authToken), "Retrieved username should match the one associated with the authToken");
    }

    @Test
    void testDeleteAuth() {
        // Given: A username
        String username = "testUser";

        // And: Creating an authorization and receiving the generated authToken
        String authToken = memoryAuthDAO.createAuth(username);

        // When: Deleting the authorization
        memoryAuthDAO.deleteAuth(authToken);

        // Then: The authToken no longer retrieves a username
        assertNull(memoryAuthDAO.getAuth(authToken), "AuthToken should not retrieve any username after deletion");
    }

    @Test
    void testAuthNonExistent() {
        // Given: An authToken that has not been created
        String nonExistentToken = "nonExistentToken";

        // When & Then: Retrieving a non-existent authToken should return null
        assertNull(memoryAuthDAO.getAuth(nonExistentToken), "Non-existent authToken should not retrieve any username");
    }
}
