package clientTests;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;

import result.GameCreationResult;
import result.JoinGameResult;
import result.RegisterResult;
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
        RegisterResult registerResult = facade.register(username, password, email);
        assertTrue(registerResult.success());
    }

    @Test
    void registerAndLoginTest() throws Exception {
        var username = "testUser" + System.currentTimeMillis(); // Ensure unique username
        var password = "testPass";
        var email = "testEmail@example.com";

        // Register a new user
        RegisterResult registerResult = facade.register(username, password, email);
        assertTrue(registerResult.success(), "Registration should succeed");

        // Verify authToken is present
        String authToken = registerResult.authToken();
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
        RegisterResult registerResult = facade.register(username, password, email);
        assertTrue(registerResult.success(), "Registration should succeed");

        // Assuming ServerFacade can be initialized with an authToken for subsequent operations.
        ServerFacade logoutFacade = new ServerFacade(registerResult.authToken());
        Result<Void> result = logoutFacade.logout();

        assertTrue(result.isSuccess(), "Logout should be successful");

    }

    @Test
    void createGameSuccess() throws Exception {
        // Setup for registration and login
        var username = "testUserForCreateGame" + System.currentTimeMillis(); // Ensure unique username
        var password = "testPassForCreateGame";
        var email = "testEmailForCreateGame@example.com";
        RegisterResult registerResult = facade.register(username, password, email);
        assertTrue(registerResult.success(), "Registration should succeed");

        // Use the authToken for authenticated operations
        ServerFacade gameFacade = new ServerFacade(registerResult.authToken());

        // Create a new game using the authenticated facade
        var gameName = "Test Game " + System.currentTimeMillis(); // Unique game name
        GameCreationResult createGameResult = gameFacade.createGame(gameName);

        // Check if a game ID (or success message) is returned
        assertTrue(createGameResult.success(), "Game should be created successfully");
    }



    @Test
    void createGameFailure() throws Exception {
        // Assuming there's a way to simulate failure (e.g., invalid game name or not logged in)
        var gameName = ""; // Potentially invalid game name to trigger failure
        GameCreationResult result = facade.createGame(gameName);
        assertFalse(result.success(), "Game creation should fail with invalid game name");
    }

    @Test
    void listGamesSuccess() throws Exception {
        // Setup for registration and login to ensure a valid session
        var username = "testUserForListGames" + System.currentTimeMillis(); // Ensure unique username
        var password = "testPassForListGames";
        var email = "testEmailForListGames@example.com";
        RegisterResult registerResult = facade.register(username, password, email);
        assertTrue(registerResult.success(), "Registration should succeed");


        // Use the authToken for authenticated operations
        ServerFacade listGamesFacade = new ServerFacade(registerResult.authToken());

        // Attempt to list games using the authenticated facade
        Result<String> result = listGamesFacade.listGames();
        assertTrue(result.isSuccess(), "Should successfully list games");

        // Assuming result.getData() returns a JSON string of game data
        String jsonData = result.getData();
        assertNotNull(jsonData, "Should return a JSON string of games");

        // Parse the JSON data to verify its structure
        JsonObject responseObject = JsonParser.parseString(jsonData).getAsJsonObject();
        assertTrue(responseObject.has("games"), "JSON should have a 'games' array");

        JsonArray gamesArray = responseObject.getAsJsonArray("games");
        assertNotNull(gamesArray, "The 'games' array should not be null");
        assertFalse(gamesArray.isEmpty(), "Games list should not be empty");
    }

    @Test
    void listGamesFailure() throws Exception {
        // Directly using an invalid token for this test
        facade = new ServerFacade("invalidToken");
        Result<String> result = facade.listGames();
        assertFalse(result.isSuccess(), "Should fail to list games due to invalid token");
    }

    @Test
    void joinGameSuccess() throws Exception {
        // Setup for registration and login to ensure a valid session
        var username = "testUserForJoinGame" + System.currentTimeMillis(); // Ensure unique username
        var password = "testPassForJoinGame";
        var email = "testEmailForJoinGame@example.com";

        RegisterResult registerResult = facade.register(username, password, email);
        assertTrue(registerResult.success(), "Registration should succeed");

        // Assume you have a way to create or get a valid game ID. Here, we're assuming it's created and known.
        var gameName = "Test Game " + System.currentTimeMillis();
        ServerFacade facade = new ServerFacade(registerResult.authToken());
        GameCreationResult createGameResult = facade.createGame(gameName);
        assertTrue(createGameResult.success(), "Game creation should be successful");
        // Assuming the game creation result gives you the game ID

        String userColor = "WHITE"; // Assuming you choose WHITE for this test
        JoinGameResult joinGameResult = facade.joinGame(createGameResult.gameID(), userColor);

        assertTrue(joinGameResult.success(), "Joining game should be successful");
    }



    @Test
    void joinGameFailure() throws Exception {
        // Assuming setup for a logged-in user
        int invalidGameId = 0;
        String userColor = "WHITE"; // or "BLACK"

        // Perform the join game action
        JoinGameResult result = facade.joinGame(invalidGameId, userColor);
        assertFalse(result.success(), "Joining game with invalid ID should fail");
        assertNotNull(result.message(), "Error message should not be null");
    }



    @Test
    void joinGameAsObserverSuccess() throws Exception {
        // Setup for registration and login to ensure a valid session
        var username = "testUserForJoinGameAsObserver" + System.currentTimeMillis(); // Ensure unique username
        var password = "testPassForJoinGameAsObserver";
        var email = "testEmailForJoinGameAsObserver@example.com";

        RegisterResult registerResult = facade.register(username, password, email);
        assertTrue(registerResult.success(), "Registration should succeed");

        var gameName = "Test Game " + System.currentTimeMillis();
        ServerFacade facade = new ServerFacade(registerResult.authToken());
        GameCreationResult createGameResult = facade.createGame(gameName);
        assertTrue(createGameResult.success(), "Game creation should be successful");

        JoinGameResult joinGameResult = facade.joinAsObserver(createGameResult.gameID());

        assertTrue(joinGameResult.success(), "Joining game should be successful");

    }

    @Test
    void JoinGameAsObserverFailure() throws Exception{
        int invalidGameId = 0;
        // Perform the join game action
        JoinGameResult result = facade.joinAsObserver(invalidGameId);
        assertFalse(result.success(), "Joining game with invalid ID should fail");
        assertNotNull(result.message(), "Error message should not be null");
    }

    // Additional tests for createGame, listGames, joinGame, and joinAsObserver can follow a similar structure.
}
