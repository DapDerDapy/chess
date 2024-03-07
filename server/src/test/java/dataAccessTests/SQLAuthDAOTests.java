package dataAccessTests;

import dataAccess.SQLAuthDAO;
import dataAccess.DatabaseManager;
import org.junit.jupiter.api.*;

import static org.junit.jupiter.api.Assertions.*;

public class SQLAuthDAOTests {

    private SQLAuthDAO sqlAuthDAO;

    @BeforeEach
    void setUp() throws Exception {
        // Ensure the database is clean before each test
        DatabaseManager.clear();
        DatabaseManager.setupDatabaseTables();
        sqlAuthDAO = new SQLAuthDAO();
    }

    @AfterEach
    void tearDown() throws Exception {
        // Clean up the database after tests
        DatabaseManager.clear();
    }

    @Test
    void testCreateAndGetAuth() throws Exception {
        // Given: A username
        String username = "testUser";

        // When: Creating an authorization and receiving the generated authToken
        String authToken = sqlAuthDAO.createAuth(username);

        // Then: The authToken can be retrieved and is associated with the correct username
        assertEquals(username, sqlAuthDAO.getAuth(authToken), "Retrieved username should match the one associated with the authToken");
    }

    @Test
    void testDeleteAuth() throws Exception {
        // Given: A username
        String username = "testUser";

        // And: Creating an authorization and receiving the generated authToken
        String authToken = sqlAuthDAO.createAuth(username);

        // When: Deleting the authorization
        sqlAuthDAO.deleteAuth(authToken);

        // Then: The authToken no longer retrieves a username
        assertNull(sqlAuthDAO.getAuth(authToken), "AuthToken should not retrieve any username after deletion");
    }

    @Test
    void testAuthNonExistent() throws Exception {
        // Given: An authToken that has not been created
        String nonExistentToken = "nonExistentToken";

        // When & Then: Retrieving a non-existent authToken should return null
        assertNull(sqlAuthDAO.getAuth(nonExistentToken), "Non-existent authToken should not retrieve any username");
    }

    @Test
    void testIsValidToken() throws Exception {
        // Given: A username and the associated authToken
        String username = "testUser";
        String authToken = sqlAuthDAO.createAuth(username);

        // When & Then: Check if the token is valid
        assertTrue(sqlAuthDAO.isValidToken(authToken), "The authToken should be valid");

        // And: Check if a non-existent token is not valid
        assertFalse(sqlAuthDAO.isValidToken("nonExistentToken"), "Non-existent authToken should not be valid");
    }

    @Test
    void testClearAll() throws Exception {
        // Given: Multiple authTokens are created
        sqlAuthDAO.createAuth("user1");
        sqlAuthDAO.createAuth("user2");

        // When: Clearing all auth tokens
        sqlAuthDAO.clearAll();

        // Then: No auth tokens should be valid anymore
        assertFalse(sqlAuthDAO.isValidToken("user1"), "All auth tokens should be invalid after clearAll");
        assertFalse(sqlAuthDAO.isValidToken("user2"), "All auth tokens should be invalid after clearAll");
    }
}
