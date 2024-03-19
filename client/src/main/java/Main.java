import chess.*;
import server.Server;
import service.UserService;
import dataAccess.*;

public class Main {
    public static void main(String[] args) {
        var piece = new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.PAWN);
        System.out.println("♕ 240 Chess Client: " + piece);

        Server server = new Server();
        // Start the server on port 8080
        int port = server.run(8080);
        System.out.println("Server running on port " + port);
        PreloginUI ui = new PreloginUI();
        ui.processUserInput();
        System.out.println("SEE YA!");


    }
}