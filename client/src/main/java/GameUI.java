import chess.ChessBoard;
import chess.ChessGame;
import chess.ChessPiece;
import chess.ChessPosition;
import java.util.Map;

public class GameUI {
    //private ChessGame game; // Assume this exists and has methods to interact with the game
    private ChessBoard board;
    // Unicode symbols for chess pieces
    private static final Map<ChessPiece.PieceType, String> whitePieceSymbols = Map.of(
            ChessPiece.PieceType.PAWN, "♟",
            ChessPiece.PieceType.KNIGHT, "♞",
            ChessPiece.PieceType.BISHOP, "♝",
            ChessPiece.PieceType.ROOK, "♜",
            ChessPiece.PieceType.QUEEN, "♛",
            ChessPiece.PieceType.KING, "♚"
    );

    // Map for black chess pieces Unicode symbols
    private static final Map<ChessPiece.PieceType, String> blackPieceSymbols = Map.of(
            ChessPiece.PieceType.PAWN, "♙",
            ChessPiece.PieceType.KNIGHT, "♘",
            ChessPiece.PieceType.BISHOP, "♗",
            ChessPiece.PieceType.ROOK, "♖",
            ChessPiece.PieceType.QUEEN, "♕",
            ChessPiece.PieceType.KING, "♔"
    );

    // ANSI colors for the pieces and the board
    private final String ANSI_RESET = "\u001B[0m";
    private final String ANSI_RED = "\u001B[31m";
    private final String ANSI_BLUE = "\u001B[34m";

    private final String ANSI_BLACK = "\u001B[30m";
    private final String ANSI_WHITE = "\u001B[37m";

    private final String ANSI_WHITE_BACKGROUND = "\u001B[47m";
    private final String ANSI_BLACK_BACKGROUND = "\u001B[40m";

    private final String EM_SPACE = "\u2003";
    private final String BLACK_PERSPECTIVE_LETTERS = "   h " + EM_SPACE + "g "+ EM_SPACE + "f " + EM_SPACE + "e " + EM_SPACE +  "d " + EM_SPACE + "c " + EM_SPACE + "b " +EM_SPACE+ "a";
    private final String WHITE_PERSPECTIVE_LETTERS = "   a " + EM_SPACE + "b " + EM_SPACE + "c " + EM_SPACE + "d " + EM_SPACE + "e " + EM_SPACE + "f " + EM_SPACE + "g " + EM_SPACE + "h";


    public GameUI(ChessBoard board) {
        this.board = board;
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
        System.out.println(WHITE_PERSPECTIVE_LETTERS);
        for (int row = 8; row >= 1; row--) {
            System.out.print(row + " ");
            for (char col = 'a'; col <= 'h'; col++) {
                printSquare(col, row);
            }
            System.out.println(" " + row);
        }
        System.out.println(WHITE_PERSPECTIVE_LETTERS);
    }

    private void displayBoardFromBlackPerspective() {
        System.out.println(BLACK_PERSPECTIVE_LETTERS);
        for (int row = 1; row <= 8; row++) {
            System.out.print(row + " ");
            for (char col = 'h'; col >= 'a'; col--) {
                printSquare(col, row);
            }
            System.out.println(" " + row);
        }
        System.out.println(BLACK_PERSPECTIVE_LETTERS);
    }

    private void printSquare(char col, int row) {
        int colIndex = col - 'a' + 1;
        ChessPosition position = new ChessPosition(row, colIndex);
        ChessPiece piece = board.getPiece(position);

        // Determine the background color
        boolean isWhiteSquare = (row + colIndex) % 2 == 0;
        String backgroundColor = isWhiteSquare ? ANSI_WHITE_BACKGROUND : ANSI_BLACK_BACKGROUND;

        // Print the chess piece or a space if no piece is present
        String pieceSymbol = (piece != null) ? getUnicodeSymbol(piece) : EM_SPACE; // Em-space for empty squares
        System.out.print(backgroundColor +  " " + pieceSymbol + " " + ANSI_RESET);
    }


    private String getUnicodeSymbol(ChessPiece piece) {
        if (piece.getTeamColor() == ChessGame.TeamColor.WHITE) {
            return whitePieceSymbols.get(piece.getPieceType());
        } else {
            return blackPieceSymbols.get(piece.getPieceType());
        }
    }

}
