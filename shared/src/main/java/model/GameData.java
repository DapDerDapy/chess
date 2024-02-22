package model;
import chess.ChessGame;

public record GameData(int id, String blackUsername, String whiteUsername,
                       String gameName, ChessGame game) { }
