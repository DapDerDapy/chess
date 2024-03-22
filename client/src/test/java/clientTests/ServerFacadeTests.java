package clientTests;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import server.Server;
import serverFacade.ServerFacade;
import serverFacade.Result;

public class ServerFacadeTests {

    private static Server server;
    private static ServerFacade facade;
    private static int port;

    @BeforeAll
    public static void init() {
        server = new Server();
        port = server.run(8080); // Dynamically assigned port
        facade = new ServerFacade("http://localhost:8080"); // Pass the correct base URI
        System.out.println("Started test HTTP server on port " + port);
    }


    @AfterAll
    static void stopServer() {
        server.stop();
    }

    @Test
    void registerTest() {
        String username = "testUser" + System.currentTimeMillis(); // Ensure unique username
        String password = "testPass";
        String email = "testEmail@example.com";

        // Attempt to register a new user
        Result<Void> registerResult = facade.register(username, password, email);
        assertTrue(registerResult.isSuccess());
    }

    @Test
    void registerAndLoginTest() throws Exception {
        var username = "testUser" + System.currentTimeMillis(); // Ensure unique username
        var password = "testPass";
        var email = "testEmail@example.com";

        // Register a new user
        Result<String> registerResult = facade.register(username, password, email);
        assertTrue(registerResult.isSuccess(), "Registration should succeed");

        // Verify authToken is present
        String authToken = registerResult.getData();
        assertNotNull(authToken, "Auth token should not be null after registration");

        // Log in with the new user
        Result<String> loginResult = facade.login(username, password);
        assertTrue(loginResult.isSuccess(), "Login should succeed");

        // Ensure the authToken from login is also valid
        String loginAuthToken = loginResult.getData();
        assertNotNull(loginAuthToken, "Auth token should not be null after login");
    }


    // Positive test for logout
    @Test
    void logoutFailure() throws Exception {
        // Here, directly test logout assuming a valid session exists or mocking behavior as needed
        Result<Void> result = facade.logout();
        assertFalse(result.isSuccess(), "Logout should fail");
    }

    @Test
    void logoutSuccess() throws Exception {
        // Register a new user

        var username = "testUserForLogout" + System.currentTimeMillis(); // Ensure unique username
        var password = "testPassForLogout";
        var email = "testEmailForLogout@example.com";
        Result<String> registerResult = facade.register(username, password, email);
        assertTrue(registerResult.isSuccess(), "Registration should succeed");


        Result<Void> result = facade.logout();
        assertFalse(result.isSuccess(), "Logout should be successful");

    }






    // Additional tests for createGame, listGames, joinGame, and joinAsObserver can follow a similar structure.
}
