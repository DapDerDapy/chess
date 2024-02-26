package serviceTests;

import dataAccess.AuthDAO;
import dataAccess.UserDAO;
import model.UserData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import request.RegisterRequest;
import result.RegisterResult;
import service.UserService;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

public class UserServiceTest {

    private UserDAO userDAO;
    private AuthDAO authDAO;
    private UserService userService;

    @BeforeEach
    void setUp() {
        // Mock the DAOs
        userDAO = Mockito.mock(UserDAO.class);
        authDAO = Mockito.mock(AuthDAO.class);

        // Instantiate UserService with the mocked DAOs
        userService = new UserService(userDAO, authDAO);
    }

    @Test
    void testRegisterSuccess() {
        // Arrange: Simulate that the user does not already exist
        when(userDAO.getUser(any(String.class))).thenReturn(null);
        // Simulate successful authToken creation
        when(authDAO.createAuth(any(String.class))).thenReturn("testAuthToken");

        RegisterRequest request = new RegisterRequest("testUser", "testPassword", "testEmail");

        // Act: Attempt to register the user
        RegisterResult result = userService.register(request);

        // Assert: Verify registration was successful
        assertTrue(result.success());
        assertEquals("testUser", result.username());
        assertNotNull(result.authToken(), "Auth token should not be null");
    }

    @Test
    void testRegisterDuplicateUsername() {
        // Arrange: Simulate that the user already exists
        when(userDAO.getUser("testUser")).thenReturn(new UserData("testUser", "testPassword", "testEmail"));

        RegisterRequest request = new RegisterRequest("testUser", "testPassword", "testEmail");

        // Act: Attempt to register the user
        RegisterResult result = userService.register(request);

        // Assert: Verify registration failed due to duplicate username
        assertFalse(result.success());
        assertNull(result.authToken(), "Auth token should be null on failure");
    }
}
