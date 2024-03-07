package serviceTests;

import chess.ChessGame;
import dataAccess.AuthDAO;
import dataAccess.GameDAO;
import dataAccess.UserDAO;
import exceptions.AlreadyTakenException;
import exceptions.InvalidGameIdException;
import model.GameData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import exceptions.AuthenticationException;
import result.*;
import request.*;
import service.GameService;

import java.util.ArrayList;
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

        // Use a generic Collection type or a List without casting to ArrayList
        Collection<GameData> expectedGames = List.of(new GameData(id, blackUsername, whiteUsername, gameName, chessGame));
        when(gameDAO.listGames()).thenReturn(expectedGames);

        // Execute
        Collection<GameData> actualGames = gameService.listGames(validToken);

        // Verify
        System.out.println(expectedGames);
        System.out.println(actualGames);
        assertNotNull(actualGames, "The actual games collection should not be null.");
        assertEquals(expectedGames.size(), actualGames.size(), "The number of games should match the expected size.");

        // Use more detailed assertions to compare the contents of the collections if necessary
        // This might include iterating through the collections and comparing individual GameData objects

        // Verify interactions
        verify(authDAO, times(1)).isValidToken(validToken);
        verify(gameDAO, times(1)).listGames();
    }

    @Test
    void listGames_MultipleGames_ValidToken_Success() throws AuthenticationException {
        // Setup
        String validToken = "validToken";
        when(authDAO.isValidToken(validToken)).thenReturn(true);

        // Create multiple game data objects
        GameData game1 = new GameData(1, "blackPlayer1", "whitePlayer1", "Game One", new ChessGame());
        GameData game2 = new GameData(2, "blackPlayer2", "whitePlayer2", "Game Two", new ChessGame());
        GameData game3 = new GameData(3, "blackPlayer3", "whitePlayer3", "Game Three", new ChessGame());


        // Prepare expected games list
        Collection<GameData> expectedGames = List.of(game1, game2, game3);
        when(gameDAO.listGames()).thenReturn(expectedGames);


        // Execute
        Collection<GameData> actualGames = gameService.listGames(validToken);

        // Verify
        assertNotNull(actualGames, "The actual games collection should not be null.");
        assertEquals(expectedGames.size(), actualGames.size(), "The size of the actual games collection should match the expected games.");

        // Optional: More detailed assertions to check if all expected games are present in the actual list
        assertTrue(actualGames.containsAll(expectedGames), "The actual games list should contain all the expected games.");

        System.out.println(expectedGames);
        System.out.println(actualGames);
        // Verify interactions
        verify(authDAO, times(1)).isValidToken(validToken);
        verify(gameDAO, times(1)).listGames();
    }


    @Test
    public void testJoinGameColorAlreadyTakenThrowsException() throws AuthenticationException, InvalidGameIdException {
        // Arrange
        String validToken = "validAuthToken";
        int gameId = 1; // Assume this is a valid game ID.
        String color = "BLACK"; // Specify the color attempted to join is "BLACK".
        JoinGameRequest request = new JoinGameRequest(gameId, color);

        // Mock the validation of the token.
        when(authDAO.isValidToken(validToken)).thenReturn(true);

        // Mock the existence of the game to simulate it exists.
        GameData gameData = new GameData(gameId, "TestUserBlack", null, "Test Game", new ChessGame());
        when(gameDAO.getGame(gameId)).thenReturn(gameData);

        // Mock the scenario where the specified color is already taken.
        when(gameDAO.isColorTaken(gameId, color)).thenReturn(true);

        // Act and Assert
        assertThrows(AlreadyTakenException.class, () -> gameService.joinGame(validToken, request),
                "Should throw AlreadyTakenException when the color is already taken.");
    }

    @Test
    public void testJoinGameSuccess() throws AuthenticationException, AlreadyTakenException, InvalidGameIdException {
        // Arrange
        String validToken = "validAuthToken";
        int gameId = 1; // Assume this is a valid game ID.
        String color = "BLACK"; // Assuming 'BLACK' color is not already taken.
        JoinGameRequest request = new JoinGameRequest(gameId, color);

        // Mock the validation of the token.
        when(authDAO.isValidToken(validToken)).thenReturn(true);

        // Mock the game data to simulate the game exists.
        GameData gameData = new GameData(gameId, "TestUserBlack", null, "Test Game", new ChessGame());
        when(gameDAO.getGame(gameId)).thenReturn(gameData);

        // Mock the scenario where the color is not taken.
        when(gameDAO.isColorTaken(gameId, color)).thenReturn(false);

        // Mock successful join game action.
        when(gameDAO.joinGame(gameId, color, validToken, "testUser")).thenReturn(true);

        // Mock retrieval of username from token.
        when(authDAO.getUsernameFromToken(validToken)).thenReturn("testUser");

        // Act
        JoinGameResult result = gameService.joinGame(validToken, request);

        // Assert
        assertTrue(result.success(), "Joining the game should be successful.");
        assertEquals("Successfully joined the game.", result.message(), "The success message should correctly indicate a successful game join.");
    }



    @Test
    public void testJoinGameInvalidAuthTokenThrowsException() {
        // Arrange
        String invalidToken = "invalidAuthToken";
        int gameId = 1; // Example game ID
        String color = "black";
        JoinGameRequest request = new JoinGameRequest(gameId, color);

        when(authDAO.isValidToken(invalidToken)).thenReturn(false); // Token is invalid

        // Act and Assert
        assertThrows(AuthenticationException.class, () -> {
            gameService.joinGame(invalidToken, request);
        }, "Should throw AuthenticationException when the auth token is invalid or expired.");
    }

}
