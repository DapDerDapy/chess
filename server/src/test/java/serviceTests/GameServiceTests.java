package serviceTests;

import chess.ChessGame;
import dataAccess.AuthDAO;
import dataAccess.GameDAO;
import model.GameData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import exceptions.AuthenticationException;
import result.GameCreationResult;
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

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        gameService = new GameService(gameDAO, authDAO);
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

}
