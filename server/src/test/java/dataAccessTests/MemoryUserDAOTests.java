package dataAccessTests;

import model.UserData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import dataAccess.MemoryUserDAO;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class MemoryUserDAOTests {

    private MemoryUserDAO memoryUserDAO;

    @BeforeEach
    void setUp() {
        memoryUserDAO = new MemoryUserDAO();
    }

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




}