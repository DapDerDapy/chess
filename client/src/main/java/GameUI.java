import chess.ChessGame;
import chess.ChessPiece;
import chess.ChessPosition;

public class GameUI {
    private ChessGame game; // Assume this exists and has methods to interact with the game

    // Unicode symbols for chess pieces
    private final String WHITE_PAWN = "\u2659";
    private final String BLACK_PAWN = "\u265F";

    private final String WHITE_KNIGHT = "\u2658";
    private final String BLACK_KNIGHT = "\u265E";

    private final String WHITE_BISHOP = "\u2657";
    private final String BLACK_BISHOP = "\u265D";

    private final String WHITE_ROOK = "\u2656";
    private final String BLACK_ROOK = "\u265C";

    private final String WHITE_QUEEN = "\u2655";
    private final String BLACK_QUEEN = "\u265B";

    private final String WHITE_KING = "\u2654";
    private final String BLACK_KING = "\u265A";

    // ANSI colors for the pieces and the board
    private final String ANSI_RESET = "\u001B[0m";
    private final String ANSI_RED = "\u001B[31m";
    private final String ANSI_BLUE = "\u001B[34m";

    private final String ANSI_BLACK = "\u001B[30m";
    private final String ANSI_WHITE = "\u001B[37m";

    // Unicode for the checkerboard
    private final String WHITE_SQUARE = "\u25A1"; // White Square (◻)
    private final String BLACK_SQUARE = "\u25A0"; // Black Square (◼)


    public GameUI(ChessGame game) {
        this.game = game;
    }

    // Display the board from both perspectives
    public void displayBoards() {
        System.out.println("From White's Perspective:");
        displayBoardFromWhitePerspective();
        System.out.println(); // Add a separator between the two boards
        System.out.println("From Black's Perspective:");
        displayBoardFromBlackPerspective();
    }

    private void displayBoardFromWhitePerspective() {
        System.out.println("  a b c d e f g h");
        for (int row = 8; row >= 1; row--) {
            System.out.print(row + " ");
            for (char col = 'a'; col <= 'h'; col++) {
                printSquare(col, row);
            }
            System.out.println(" " + row);
        }
        System.out.println("  a b c d e f g h");
    }

    private void displayBoardFromBlackPerspective() {
        System.out.println("  h g f e d c b a");
        for (int row = 1; row <= 8; row++) {
            System.out.print(row + " ");
            for (char col = 'h'; col >= 'a'; col--) {
                printSquare(col, row);
            }
            System.out.println(" " + row);
        }
        System.out.println("  h g f e d c b a");
    }


    private void printSquare(char col, int row) {
        // Calculate the color of the square based on its position
        boolean isWhiteSquare = (row + col) % 2 == 0;

        // Ensure the board is not null
        if (game.getBoard() == null) {
            System.out.print("Error during login: Cannot invoke \"chess.ChessBoard.getPiece(chess.ChessPosition)\" because the return value of \"chess.ChessGame.getBoard()\" is null");
            return;
        }

        // Get the piece at the current square
        ChessPiece piece = game.getBoard().getPiece(new ChessPosition(col, row));

        // Get the unicode symbol for the piece or a space if there is no piece
        String pieceSymbol = piece != null ? getUnicodeSymbol(piece) : " ";

        // Choose the color of the square based on its position
        String squareColor = isWhiteSquare ? ANSI_WHITE : ANSI_BLACK;

        // Print the square with the piece
        System.out.print(squareColor + pieceSymbol + ANSI_RESET);

        // After the piece, print an extra space to separate from the next square
        System.out.print(" ");
    }


    private String getUnicodeSymbol(ChessPiece piece) {
        // Return the unicode symbol for the piece, assuming you have a method isWhite() to check the color
        if (piece.getTeamColor() == ChessGame.TeamColor.WHITE) {
            switch (piece.getPieceType()) {
                case PAWN:
                    return WHITE_PAWN;
                case KNIGHT:
                    return WHITE_KNIGHT;
                case BISHOP:
                    return WHITE_BISHOP;
                case ROOK:
                    return WHITE_ROOK;
                case QUEEN:
                    return WHITE_QUEEN;
                case KING:
                    return WHITE_KING;
            }
        } else {
            switch (piece.getPieceType()) {
                case PAWN:
                    return BLACK_PAWN;
                case KNIGHT:
                    return BLACK_KNIGHT;
                case BISHOP:
                    return BLACK_BISHOP;
                case ROOK:
                    return BLACK_ROOK;
                case QUEEN:
                    return BLACK_QUEEN;
                case KING:
                    return BLACK_KING;
            }
        }
        return " "; // If no piece is found or other error occurs
    }

}
