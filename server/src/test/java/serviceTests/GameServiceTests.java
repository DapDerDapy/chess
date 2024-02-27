package serviceTests;

import chess.ChessGame;
import dataAccess.AuthDAO;
import dataAccess.GameDAO;
import dataAccess.UserDAO;
import exceptions.AlreadyTakenException;
import model.GameData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import exceptions.AuthenticationException;
import result.*;
import request.*;
import service.GameService;
import java.util.List;
import java.util.Collection;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class GameServiceTests {

    @Mock
    private AuthDAO authDAO;

    @Mock
    private GameDAO gameDAO;

    private GameService gameService;

    private UserDAO userDAO;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        gameService = new GameService(gameDAO, authDAO, userDAO);
    }

    @Test
    void testCreateGameSuccess() throws AuthenticationException {
        // Setup
        String authToken = "validToken";
        String gameName = "TestGame";
        String blackUsername = "BlackPlayer";
        String whiteUsername = "WhitePlayer";
        ChessGame chessGame = new ChessGame(); // Assuming you have a default constructor or any constructor to create a game instance

        when(authDAO.isValidToken(authToken)).thenReturn(true);

        // Assuming gameDAO.createGame now returns the ID of the created game
        // Let's say the game ID of the newly created game is 1
        int expectedGameId = 1;
        when(gameDAO.createGame(anyString(), anyString(), anyString(), any())).thenReturn(expectedGameId);

        // Execute
        GameCreationResult result = gameService.createGame(authToken, gameName, blackUsername, whiteUsername, chessGame);

        // Verify the result
        assertNotNull(result, "The result should not be null.");
        assertTrue(result.success(), "Game creation should be successful.");
        assertEquals(expectedGameId, result.gameID(), "The game ID should match the expected value.");

        // Verify that the methods were called as expected
        verify(authDAO, times(1)).isValidToken(authToken);
        verify(gameDAO, times(1)).createGame(eq(gameName), eq(blackUsername), eq(whiteUsername), eq(chessGame));
    }


    @Test
    void testCreateGameWithInvalidToken() {
        // Setup
        String invalidAuthToken = "invalidToken";

        when(authDAO.isValidToken(invalidAuthToken)).thenReturn(false);

        // Execute & Verify
        assertThrows(AuthenticationException.class, () -> {
            gameService.createGame(invalidAuthToken, "TestGame", "BlackPlayer", "WhitePlayer", new ChessGame());
        });

        // Verify that no game is created when authToken is invalid
        verify(gameDAO, never()).createGame(anyString(), anyString(), anyString(), any(ChessGame.class));
    }

    @Test
    void listGames_ValidToken_Success() throws AuthenticationException {
        // Setup
        String validToken = "validToken";
        when(authDAO.isValidToken(validToken)).thenReturn(true);


        int id = 123;
        String blackUsername = "black";
        String whiteUsername = "white";
        String gameName = "game";
        ChessGame chessGame = new ChessGame();

        List<GameData> expectedGames = List.of(new GameData(id, blackUsername, whiteUsername, gameName, chessGame));
        when(gameDAO.listGames()).thenReturn(expectedGames);

        // Execute
        Collection<GameData> actualGames = gameService.listGames(validToken);

        // Verify
        assertNotNull(actualGames);
        assertEquals(expectedGames.size(), actualGames.size());
        // Further assertions can be made here based on your GameData structure

        // Verify interactions
        verify(authDAO, times(1)).isValidToken(validToken);
        verify(gameDAO, times(1)).listGames();
    }
    @Test
    public void testJoinGameColorAlreadyTakenThrowsException() {
        // Arrange
        String validToken = "validAuthToken";
        int gameId = 1; // Example game ID
        String color = "black";
        JoinGameRequest request = new JoinGameRequest(gameId, color);

        when(authDAO.isValidToken(validToken)).thenReturn(true);
        when(authDAO.getUsernameFromToken(validToken)).thenReturn("testUser");
        when(gameDAO.isColorTaken(gameId, color)).thenReturn(true);

        // Act and Assert
        assertThrows(AlreadyTakenException.class, () -> {
            gameService.joinGame(validToken, request);
        }, "Should throw AlreadyTakenException when the color is already taken.");
    }
    @Test
    public void testJoinGameSuccess() throws AuthenticationException, AlreadyTakenException {
        // Arrange
        String validToken = "validAuthToken";
        int gameId = 1; // Example game ID
        String color = "black"; // Assuming 'black' is not already taken
        JoinGameRequest request = new JoinGameRequest(gameId, color);

        when(authDAO.isValidToken(validToken)).thenReturn(true);
        when(authDAO.getUsernameFromToken(validToken)).thenReturn("testUser");
        when(gameDAO.isColorTaken(gameId, color)).thenReturn(false); // Color is not taken
        when(gameDAO.joinGame(gameId, color, validToken, "testUser")).thenReturn(true); // Simulate successful join

        // Act
        JoinGameResult result = gameService.joinGame(validToken, request);

        // Assert
        assertTrue(result.success(), "Joining the game should be successful.");
        assertEquals("Successfully joined the game.", result.message(), "The success message should indicate successful game join.");
    }
}
