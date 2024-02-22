package dataAccess;

import chess.ChessGame;
import model.GameData;
import java.util.Collection;

public interface GameDAO {

    void createGame(String gameName, String blackUsername, String whiteUsername, ChessGame game);

    GameData getGame(int gameId);

    Collection<GameData> listGames();

    boolean updateGame(int gameID, ChessGame updatedChessGame);

}
