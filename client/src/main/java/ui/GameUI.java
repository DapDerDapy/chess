package ui;

import chess.ChessBoard;
import chess.ChessGame;
import chess.ChessPiece;
import chess.ChessPosition;
import java.util.Map;
import java.util.Objects;
import java.util.Scanner;

public class GameUI {
    //private ChessGame game; // Assume this exists and has methods to interact with the game
    private ChessBoard board;
    private Scanner scanner;

    private String userColor;

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
    private final String ANSI_CYAN = "\u001B[36m";
    private final String ANSI_RED = "\u001B[31m";
    private final String ANSI_GREEN = "\u001B[32m";
    private final String ANSI_YELLOW = "\u001B[33m";
    private final String ANSI_PURPLE = "\u001B[35m";
    private final String ANSI_BLUE = "\u001B[34m";
    private final String ANSI_WHITE_BACKGROUND = "\u001B[47m";
    private final String ANSI_BLACK_BACKGROUND = "\u001B[40m";

    private final String EM_SPACE = "\u2003";
    private final String BLACK_PERSPECTIVE_LETTERS = "   h " + EM_SPACE + "g "+ EM_SPACE + "f " + EM_SPACE + "e " + EM_SPACE +  "d " + EM_SPACE + "c " + EM_SPACE + "b " +EM_SPACE+ "a";
    private final String WHITE_PERSPECTIVE_LETTERS = "   a " + EM_SPACE + "b " + EM_SPACE + "c " + EM_SPACE + "d " + EM_SPACE + "e " + EM_SPACE + "f " + EM_SPACE + "g " + EM_SPACE + "h";


    public GameUI(ChessBoard board, String userColor) {
        this.board = board;
        this.userColor = userColor;
        this.scanner = new Scanner(System.in);
    }

    public void displayMenu(){
        System.out.println( "1. " + ANSI_CYAN + "Help" + ANSI_RESET);
        System.out.println("2. " + ANSI_PURPLE + "Redraw Chessboard" + ANSI_RESET);
        System.out.println("3. " + ANSI_RED + "Leave" + ANSI_RESET);
        System.out.println("4. " + ANSI_GREEN + "Make Move!" + ANSI_RESET);
        System.out.println("5. " + ANSI_RED +  "Resign" + ANSI_RESET);
        System.out.println("6. " + ANSI_YELLOW + "Highlight Legal Moves" + ANSI_RESET);
        System.out.print("Please enter your choice: ");
    }



    public void processUserInput() {
        boolean keepRunning = true;

        while (keepRunning) {
            displayMenu();
            String input = scanner.nextLine();

            switch (input) {
                case "1":
                    displayHelp();
                    break;
                case "2":
                    redrawChessboard();
                    break;
                case "3":
                    keepRunning = false; // Returns to PostLoginUI
                    break;
                case "4":
                    makeMove();
                    break;
                case "5":
                    resignGame();
                    break;
                case "6":
                    highlightLegalMoves();
                    break;
                default:
                    System.out.println(ANSI_RED + "Invalid input. Please try again." + ANSI_RESET);
                    break;
            }
        }
    }

    public void redrawChessboard(){
        if (Objects.equals(userColor, "WHITE")){
            displayBoardFromWhitePerspective();
        } else if (Objects.equals(userColor, "BLACK")) {
            displayBoardFromBlackPerspective();
        } else {
            displayBoards();;
        }
    }

    private void highlightLegalMoves() {
        // TODO: take the available squares from the available moves from piecemoves and take those squares and highlight them green
    }

    private void resignGame() {

    }

    private void makeMove() {

    }

    private void displayHelp() {
        System.out.println(ANSI_CYAN + "Game Help:" + ANSI_RESET);
        System.out.println("- Type '1' to see this help message.");
        System.out.println("- Type '2' to reprint the Chessboard in the display");
        System.out.println("- Type '3' to leave the current game and return to PostLoginUI.");
        System.out.println("- Type '4' to select a piece you would like to move, as well as where to move it to.");
        System.out.println("- Type '5' to resign, or give up and lose the game.");
        System.out.println("- Type '6' to highlight the legal moves in green for a selected piece.");
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
