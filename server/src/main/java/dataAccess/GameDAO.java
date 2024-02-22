package dataAccess;

import chess.ChessGame;
import model.GameData;

public interface GameDAO {

    void createGame(String gameName, String blackUsername, String whiteUsername, ChessGame game);

    GameData getGame(int gameId);

}
