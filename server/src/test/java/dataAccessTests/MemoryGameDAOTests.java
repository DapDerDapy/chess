package dataAccessTests;

import dataAccess.MemoryGameDAO;
import chess.ChessGame;
import model.GameData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.util.Collection;

import static org.junit.jupiter.api.Assertions.*;

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

    @Test
    void testListGames() {
        // Given: Add two games to the DAO
        String gameName1 = "TestGame1";
        String blackUsername1 = "BlackPlayer1";
        String whiteUsername1 = "WhitePlayer1";
        ChessGame chessGame1 = new ChessGame();

        String gameName2 = "TestGame2";
        String blackUsername2 = "BlackPlayer2";
        String whiteUsername2 = "WhitePlayer2";
        ChessGame chessGame2 = new ChessGame();

        memoryGameDAO.createGame(gameName1, blackUsername1, whiteUsername1, chessGame1);
        memoryGameDAO.createGame(gameName2, blackUsername2, whiteUsername2, chessGame2);

        // When: List all games
        Collection<GameData> listedGames = memoryGameDAO.listGames();

        // Then: Verify the list contains exactly the two added games
        assertNotNull(listedGames, "The list of games should not be null");
        assertEquals(2, listedGames.size(), "There should be exactly 2 games listed");
    }

    @Test
    void testUpdateGame() {
        // Given: Create a game and note its ID
        String initialGameName = "InitialGame";
        ChessGame initialChessGame = new ChessGame();
        memoryGameDAO.createGame(initialGameName, "Player1", "Player2", initialChessGame);
        int gameId = 1; // Assuming this is the first game and its ID is 1

        // New state for updating the game
        ChessGame updatedChessGame = new ChessGame();

        // When: Update the game with the new ChessGame state
        boolean updateResult = memoryGameDAO.updateGame(gameId, updatedChessGame);

        // Then: Verify the game was updated successfully
        assertTrue(updateResult, "The game should be updated successfully");

        // Retrieve the updated game and verify its ChessGame state
        GameData updatedGame = memoryGameDAO.getGame(gameId);
        assertNotNull(updatedGame, "Updated game should not be null");
        assertEquals(updatedChessGame, updatedGame.game(), "The ChessGame state should be updated");

        // Verify update fails for a non-existent game
        assertFalse(memoryGameDAO.updateGame(999, updatedChessGame), "Update should fail for a non-existent game");
    }

}
