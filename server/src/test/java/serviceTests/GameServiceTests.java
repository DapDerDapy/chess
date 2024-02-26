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
import service.GameService;
import java.util.List;

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

        // You might need to adjust this stubbing based on your actual method signatures and return types
        GameData expectedGameData = new GameData(1, blackUsername, whiteUsername, gameName, chessGame);
        when(gameDAO.listGames()).thenReturn(List.of(expectedGameData));

        // Execute
        GameData resultGameData = gameService.createGame(authToken, gameName, blackUsername, whiteUsername, chessGame);

        // Verify
        assertNotNull(resultGameData);
        assertEquals(gameName, resultGameData.gameName());
        assertEquals(blackUsername, resultGameData.blackUsername());
        assertEquals(whiteUsername, resultGameData.whiteUsername());

        // Verify interactions
        verify(authDAO, times(1)).isValidToken(authToken);
        verify(gameDAO, times(1)).createGame(gameName, blackUsername, whiteUsername, chessGame);
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
}
