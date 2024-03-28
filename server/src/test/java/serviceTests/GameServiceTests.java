package serviceTests;

import chess.ChessGame;
import dataAccess.MemoryAuthDAO;
import dataAccess.MemoryGameDAO;
import dataAccess.MemoryUserDAO;
import exceptions.AuthenticationException;
import exceptions.InvalidGameIdException;
import model.GameData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import request.JoinGameRequest;
import result.JoinGameResult;
import service.GameService;

import java.util.Collection;

import static org.junit.jupiter.api.Assertions.*;

public class GameServiceTests {

    private MemoryAuthDAO authDAO;
    private MemoryGameDAO gameDAO;

    private MemoryUserDAO userDAO;
    private GameService gameService;

    private String validToken;
    private String invalidToken;

    @BeforeEach
    void setUp() {
        authDAO = new MemoryAuthDAO();
        gameDAO = new MemoryGameDAO();
        // Assuming an in-memory UserDAO is implemented if needed
        userDAO = new MemoryUserDAO();
        gameService = new GameService(gameDAO, authDAO, userDAO);

        // Create a valid token for positive test cases
        validToken = authDAO.createAuth("testUser");
        // Define an invalid token for negative test cases
        invalidToken = "invalidToken";
    }


    @Test
    void testCreateGameSuccess() throws AuthenticationException {
        // Setup
        String authToken = authDAO.createAuth("BlackPlayer");
        String gameName = "TestGame";
        String blackUsername = "BlackPlayer";
        String whiteUsername = "WhitePlayer";
        ChessGame chessGame = new ChessGame(); // Assuming you have a default constructor to create a game instance

        // Execute
        int gameId = gameDAO.createGame(gameName, blackUsername, whiteUsername, chessGame);
        assertTrue(gameId > 0, "The game should be successfully created and have a valid ID.");

        // Verify
        GameData gameData = gameDAO.getGame(gameId);
        assertNotNull(gameData, "The game data should not be null.");
        assertEquals(gameName, gameData.getGameName(), "The game name should match the expected value.");
    }

    @Test
    void testCreateGameWithInvalidToken() {
        // Setup
        String invalidAuthToken = "invalidToken"; // Assuming this token was never created, thus invalid

        // Execute & Verify
        AuthenticationException thrown = assertThrows(AuthenticationException.class, () -> {
            gameService.createGame(invalidAuthToken, "TestGame", "BlackPlayer", "WhitePlayer", new ChessGame());
        }, "Should throw AuthenticationException for invalid auth token.");

        assertNotNull(thrown, "An AuthenticationException should be thrown.");
    }

    @Test
    void listGames_ValidToken_Success() throws AuthenticationException {
        // Setup
        String validToken = authDAO.createAuth("testUser");

        ChessGame chessGame = new ChessGame();
        gameDAO.createGame("TestGame", "BlackPlayer", "WhitePlayer", chessGame);

        // Execute
        Collection<GameData> games = gameService.listGames(validToken);

        // Verify
        assertNotNull(games, "The list of games should not be null.");
        assertFalse(games.isEmpty(), "The list of games should not be empty.");
    }


    @Test
    void listGames_InvalidToken_ThrowsAuthenticationException() {
        assertThrows(AuthenticationException.class, () -> gameService.listGames(invalidToken),
                "Should throw AuthenticationException for invalid auth token.");
    }

    @Test
    void joinGame_ValidTokenAndAvailableColor_Success() throws Exception {
        // Setup
        int gameId = gameDAO.createGame("ChessMatch", "player1", null, new ChessGame());
        JoinGameRequest request = new JoinGameRequest(gameId, "BLACK");

        // Execute
        JoinGameResult result = gameService.joinGame(validToken, request);

        // Verify
        assertTrue(result.success(), "Joining the game should be successful.");
    }

    @Test
    void joinGame_InvalidGameId_ThrowsInvalidGameIdException() {
        // Given
        JoinGameRequest request = new JoinGameRequest(999, "WHITE"); // Assuming 999 is an invalid game ID

        // When & Then
        assertThrows(InvalidGameIdException.class, () -> gameService.joinGame(validToken, request),
                "Should throw InvalidGameIdException for non-existent game ID.");
    }

    // Similar approach for other tests...
}
