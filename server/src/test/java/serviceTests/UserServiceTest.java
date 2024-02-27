package serviceTests;

import dataAccess.AuthDAO;
import dataAccess.UserDAO;
import exceptions.AuthenticationException;
import model.UserData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import request.RegisterRequest;
import result.RegisterResult;
import request.LoginRequest;
import result.LoginResult;
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

    @Test
    void testLoginSuccess() throws AuthenticationException {
        // Arrange
        String expectedUsername = "testUser";
        String expectedPassword = "testPassword";
        String expectedAuthToken = "authToken123";
        UserData mockUser = new UserData(expectedUsername, expectedPassword, "testEmail");

        when(userDAO.getUser(expectedUsername)).thenReturn(mockUser);
        when(authDAO.createAuth(expectedUsername)).thenReturn(expectedAuthToken);

        // Act
        LoginResult result = userService.login(new LoginRequest(expectedUsername, expectedPassword, expectedAuthToken));

        // Assert
        assertEquals(expectedUsername, result.username());
        assertEquals(expectedAuthToken, result.authToken());
    }

    @Test
    void testLoginInvalidUsername() {
        // Arrange
        String invalidUsername = "nonExistentUser";
        when(userDAO.getUser(invalidUsername)).thenReturn(null);

        // Act & Assert
        assertThrows(AuthenticationException.class, () -> {
            userService.login(new LoginRequest(invalidUsername, "anyPassword","someAuthToken123"));
        });
    }
    @Test
    void testLoginIncorrectPassword() {
        // Arrange
        String username = "testUser";
        UserData mockUser = new UserData(username, "correctPassword", "testEmail");
        when(userDAO.getUser(username)).thenReturn(mockUser);

        // Act & Assert
        assertThrows(AuthenticationException.class, () -> {
            userService.login(new LoginRequest(username, "wrongPassword", "authToken"));
        });
    }

    @Test
    void testLogoutSuccess() {
        // Arrange: Assume the authToken exists and can be invalidated
        String testAuthToken = "testAuthToken";
        when(authDAO.deleteAuth(testAuthToken)).thenReturn(true);

        // Act: Attempt to log out
        boolean logoutSuccess = userService.logout(testAuthToken);

        // Assert: Verify that the logout was successful
        assertTrue(logoutSuccess, "Logout should be successful when authToken exists and is invalidated.");
    }

    @Test
    void testLogoutFailure() {
        // Arrange: Assume the authToken does not exist or cannot be invalidated
        String invalidAuthToken = "invalidAuthToken";
        when(authDAO.deleteAuth(invalidAuthToken)).thenReturn(false);

        // Act: Attempt to log out with an invalid token
        boolean logoutSuccess = userService.logout(invalidAuthToken);

        // Assert: Verify that the logout was unsuccessful
        assertFalse(logoutSuccess, "Logout should fail when authToken does not exist or cannot be invalidated.");
    }

}
