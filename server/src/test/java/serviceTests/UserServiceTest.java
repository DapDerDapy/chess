package serviceTests;

import dataAccess.MemoryAuthDAO;
import dataAccess.MemoryUserDAO;
import exceptions.AuthenticationException;
import model.UserData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import request.LoginRequest;
import request.RegisterRequest;
import result.LoginResult;
import result.RegisterResult;
import service.UserService;

import static org.junit.jupiter.api.Assertions.*;

public class UserServiceTest {

    private MemoryUserDAO userDAO;
    private MemoryAuthDAO authDAO;
    private UserService userService;

    @BeforeEach
    void setUp() {
        userDAO = new MemoryUserDAO();
        authDAO = new MemoryAuthDAO();
        userService = new UserService(userDAO, authDAO);
    }

    @Test
    void testRegisterSuccess() {
        // Given
        RegisterRequest request = new RegisterRequest("testUser", "testPassword", "testEmail");

        // When
        RegisterResult result = userService.register(request);

        // Then
        assertTrue(result.success());
        assertEquals("testUser", result.username());
        assertNotNull(result.authToken(), "Auth token should not be null");
    }

    @Test
    void testRegisterDuplicateUsername() {
        // Given
        userDAO.addUser(new UserData("testUser", "hashedPassword", "testEmail"));
        RegisterRequest request = new RegisterRequest("testUser", "testPassword", "testEmail");

        // When
        RegisterResult result = userService.register(request);

        // Then
        assertFalse(result.success());
        assertNull(result.authToken(), "Auth token should be null on failure");
    }

    @Test
    void testLoginSuccess() throws AuthenticationException {
        // Given
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        String username = "testUser";
        String password = "testPassword";
        userDAO.addUser(new UserData(username, encoder.encode(password), "testEmail"));
        String expectedAuthToken = authDAO.createAuth(username); // Simulating token creation for consistency in test

        // When
        LoginRequest loginRequest = new LoginRequest(username, password, expectedAuthToken);
        LoginResult result = userService.login(loginRequest);

        // Then
        assertEquals(username, result.username());
        assertNotNull(result.authToken(), "Auth token should not be null");
    }

    @Test
    void testLoginInvalidUsername() {
        // Given
        String invalidUsername = "nonExistentUser";

        // When & Then
        assertThrows(AuthenticationException.class, () -> userService.login(new LoginRequest(invalidUsername, "anyPassword", "someToken")),
                "Should throw AuthenticationException for invalid username.");
    }

    @Test
    void testLoginIncorrectPassword() {
        // Given
        String username = "testUser";
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        userDAO.addUser(new UserData(username, encoder.encode("correctPassword"), "testEmail"));

        // When & Then
        assertThrows(AuthenticationException.class, () -> userService.login(new LoginRequest(username, "wrongPassword", "wrongToken")),
                "Should throw AuthenticationException for incorrect password.");
    }

    @Test
    void testLogoutSuccess() {
        // Given
        String authToken = authDAO.createAuth("testUser"); // Create an authToken to be deleted

        // When
        boolean logoutSuccess = userService.logout(authToken);

        // Then
        assertTrue(logoutSuccess, "Logout should be successful when authToken exists and is invalidated.");
    }

    @Test
    void testLogoutFailure() {
        // Given
        String invalidAuthToken = "invalidAuthToken"; // Assume this token was never created or already invalidated

        // When
        boolean logoutSuccess = userService.logout(invalidAuthToken);

        // Then
        assertFalse(logoutSuccess, "Logout should fail when authToken does not exist or cannot be invalidated.");
    }
}
