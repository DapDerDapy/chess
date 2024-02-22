package dataAccessTests;

import dataAccess.MemoryGameDAO;
import chess.ChessGame;
import model.GameData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class MemoryGameDAOTests {

    private MemoryGameDAO memoryGameDAO;

    @BeforeEach
    void setUp() {
        memoryGameDAO = new MemoryGameDAO();
    }

    @Test
    void testCreateGame() {
        // Given
        String gameName = "TestGame";
        String blackUsername = "BlackPlayer";
        String whiteUsername = "WhitePlayer";
        ChessGame chessGame = new ChessGame();

        // When
        memoryGameDAO.createGame(gameName, blackUsername, whiteUsername, chessGame);

        // Then
        GameData createdGame = memoryGameDAO.getGame(1); // Assuming getGame(int id) method exists
        assertNotNull(createdGame, "The game should not be null");
        assertEquals(gameName, createdGame.gameName(), "Game name should match");
        assertEquals(blackUsername, createdGame.blackUsername(), "Black username should match");
        assertEquals(whiteUsername, createdGame.whiteUsername(), "White username should match");
        assertEquals(chessGame, createdGame.game(), "ChessGame object should match");
    }
}
