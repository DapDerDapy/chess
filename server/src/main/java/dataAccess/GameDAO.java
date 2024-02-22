package dataAccess;

import chess.ChessGame;

public interface GameDAO {

    void createGame(String blackUsername, String whiteUsername, String gameName, ChessGame game);

}
