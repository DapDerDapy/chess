package dataAccess;

import chess.ChessGame;
import model.GameData;
import request.JoinGameRequest;

import java.util.Collection;

public interface GameDAO {

    int createGame(String gameName, String blackUsername, String whiteUsername, ChessGame game);

    GameData getGame(int gameId);

    Collection<GameData> listGames();

    boolean updateGame(int gameID, ChessGame updatedChessGame);

    boolean joinGame(int gameID, String color, String authToken, String username);

    boolean isColorTaken(int gameID, String color);

    boolean rejoinPlayer(int gameID, String color, String username);

    boolean joinObserverChecks(int gameId, String authToken);

    ChessGame.TeamColor getPlayerColor(int gameId, String authToken);

    void clearAll();
}
