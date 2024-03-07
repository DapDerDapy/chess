package dataAccessTests;

import dataAccess.SQLGameDAO;
import dataAccess.DatabaseManager;
import chess.ChessGame;
import model.GameData;
import org.junit.jupiter.api.*;

import java.util.Collection;

import static org.junit.jupiter.api.Assertions.*;

public class SQLGameDAOTests {
    private SQLGameDAO sqlGameDAO;

    @BeforeEach
    void setUp() throws Exception {
        // Ensure the database is clean before each test
        DatabaseManager.clear(); // Implement this method to clear your database
        sqlGameDAO = new SQLGameDAO();
        DatabaseManager.setupDatabaseTables();
    }

    @AfterEach
    void tearDown() throws Exception {
        // Clean up database after each test
        DatabaseManager.clear();
    }

    @Test
    void testCreateAndGetGame() {
        String gameName = "Chess Match";
        String blackUsername = "player1";
        String whiteUsername = "player2";
        ChessGame chessGame = new ChessGame(); // Assume a default constructor

        int gameId = sqlGameDAO.createGame(gameName, blackUsername, whiteUsername, chessGame);
        GameData gameData = sqlGameDAO.getGame(gameId);

        assertNotNull(gameData, "GameData should not be null");
        assertEquals(gameName, gameData.getGameName(), "Game names should match");
        // Further assertions can be made here depending on your implementation of ChessGame serialization/deserialization
    }

    @Test
    void testListGames() {
        // Assuming you have a method to add games in SQLGameDAO
        sqlGameDAO.createGame("Chess 1", "player1", "player2", new ChessGame());
        sqlGameDAO.createGame("Chess 2", "player3", "player4", new ChessGame());

        Collection<GameData> games = sqlGameDAO.listGames();
        assertTrue(games.size() >= 2, "Should list at least two games");
    }

    @Test
    void testUpdateGame() {
        // Create a game first
        int gameId = sqlGameDAO.createGame("UpdateTest", "player1", "player2", new ChessGame());
        ChessGame updatedChessGame = new ChessGame(); // Assume some changes have been made

        assertTrue(sqlGameDAO.updateGame(gameId, updatedChessGame), "Game should be updated successfully");
    }

    @Test
    void testJoinGameAndColorTaken() {
        int gameId = sqlGameDAO.createGame("JoinTest", "player1", "player2", new ChessGame());
        // Attempt to join the game as black when black is already taken
        assertFalse(sqlGameDAO.joinGame(gameId, "BLACK", "token123", "newPlayer"), "Joining as taken color should fail");
    }

    @Test
    void testClearAll() {
        sqlGameDAO.createGame("ClearTest", "player1", "player2", new ChessGame());
        sqlGameDAO.clearAll();

        assertTrue(sqlGameDAO.listGames().isEmpty(), "All games should be cleared");
    }
}
