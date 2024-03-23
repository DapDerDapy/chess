package model;
import chess.ChessGame;

public class GameData {


    private int gameID;

    public int getGameID() {
        return gameID;
    }

    public void setGameID(int gameID) {
        this.gameID = gameID;
    }

    public String getBlackUsername() {
        return blackUsername;
    }

    public void setBlackUsername(String blackUsername) {
        this.blackUsername = blackUsername;
    }

    public String getWhiteUsername() {
        return whiteUsername;
    }

    public void setWhiteUsername(String whiteUsername) {
        this.whiteUsername = whiteUsername;
    }

    public String getGameName() {
        return gameName;
    }

    public void setGameName(String gameName) {
        this.gameName = gameName;
    }

    public ChessGame getGame() {
        return game;
    }

    public void setGame(ChessGame game) {
        this.game = game;
    }

    private String blackUsername;
    private String whiteUsername;
    private String gameName;
    private ChessGame game;

    public GameData(int gameID, String whiteUsername, String blackUsername, String gameName, ChessGame game){
        this.gameID = gameID;
        this.whiteUsername = whiteUsername;
        this.blackUsername = blackUsername;
        this.gameName = gameName;
        this.game = game;
    }

    @Override
    public String toString() {
        // ANSI Color Codes
        String ANSI_RESET = "\u001B[0m";
        String ANSI_CYAN = "\u001B[36m";
        String ANSI_PURPLE = "\u001B[35m";
        String ANSI_GREEN = "\u001B[32m";
        String ANSI_RED = "\u001B[31m";

        return ANSI_CYAN + "Game ID: " + ANSI_RESET + gameID +
                ANSI_PURPLE + ", Game Name: " + ANSI_RESET + gameName +
                ANSI_GREEN + ", White Player: " + ANSI_RESET + whiteUsername +
                ANSI_RED + ", Black Player: " + ANSI_RESET + blackUsername + "\n";
    }


}
