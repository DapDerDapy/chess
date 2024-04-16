package ui;

import chess.*;
import com.google.gson.Gson;
import webSocketMessages.userCommands.Leave;
import websocket.WSClientEndpoint;

import java.io.IOException;
import java.net.URI;
import java.util.*;

public class GameUI {
    //private ChessGame game; // Assume this exists and has methods to interact with the game
    private Scanner scanner;

    private String userColor;

    private int gameId;

    private String authToken;

    private WSClientEndpoint wsClient;

    private ChessGame game;

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


    public GameUI(String userColor, int gameId, String authToken, URI endpointURI, ChessGame game) {
        this.game = game;
        this.userColor = userColor;
        this.scanner = new Scanner(System.in);
        this.gameId = gameId;
        this.authToken = authToken;
        this.wsClient = new WSClientEndpoint(endpointURI, this::updateGame);
        this.wsClient.connect();
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

    private void updateGame(ChessGame updatedGame) {
        this.game = updatedGame;
        redrawChessboard();  // Redraw the board with the updated game state
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
                    // This is essentially the "Leave" function
                    leaveGame();
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

    public void redrawChessboard() {
        redrawChessboard(Collections.emptySet()); // Call the overloaded method without highlights
    }

    public void redrawChessboard(Set<ChessPosition> highlightPositions) {
        if (Objects.equals(userColor, "WHITE")){
            displayBoardFromWhitePerspective(highlightPositions);
        } else if (Objects.equals(userColor, "BLACK")) {
            displayBoardFromBlackPerspective(highlightPositions);
        } else {
            displayBoards(highlightPositions);; // Adjust this method similarly if needed
        }
    }

    private void highlightLegalMoves() {
        System.out.println("Select a piece to highlight legal moves.");
        ChessPosition position = promptForPosition();
        if (position == null) return; // User cancelled or invalid input

        ChessPiece piece = game.getBoard().getPiece(position);
        if (piece == null) {
            System.out.println("No piece at the specified position.");
            return;
        }

        Collection<ChessMove> legalMoves = piece.pieceMoves(game.getBoard(), position);
        Set<ChessPosition> legalPositions = new HashSet<>();
        for (ChessMove move : legalMoves) {
            legalPositions.add(move.getEndPosition());
        }

        redrawChessboard(legalPositions); // Redraw the board with highlighted moves
    }


    private void leaveGame() {
        Leave command = new Leave(authToken, gameId);
        String message = new Gson().toJson(command);
        sendWebSocketMessage(message);
        System.out.println(ANSI_GREEN + "You have left the game." + ANSI_RESET);
    }


    private void resignGame() {

    }

    private void makeMove() {
        try {
            System.out.println("What piece would you like to move?");
            ChessPosition startPosition = promptForPosition();
            if (startPosition == null) return;  // Error handling or user cancellation.

            ChessPiece piece = game.getBoard().getPiece(startPosition);  // Access the board from ChessGame
            if (piece == null) {
                System.out.println("There is no piece at the specified position. Try again.");
                return;
            }

            if (!Objects.equals(piece.getTeamColor().toString(), userColor)) {
                System.out.println("You can't move the opponent's pieces!");
                return;
            }

            Collection<ChessMove> availableMoves = piece.pieceMoves(game.getBoard(), startPosition);  // Assuming `pieceMoves` returns legal moves.
            if (availableMoves.isEmpty()) {
                System.out.println("No legal moves available for " + piece.getPieceType());
                return;
            }

            System.out.println("Selected " + piece.getPieceType() + ". Available moves:");
            availableMoves.forEach(move -> System.out.println(" " + move.getEndPosition().getRow() + " - " + (char)(move.getEndPosition().getColumn() - 1 + 'a')));
            System.out.println("Enter the target position for your piece:");

            ChessPosition targetPosition = promptForPosition();
            if (targetPosition == null) return;

            ChessMove proposedMove = new ChessMove(startPosition, targetPosition, null);
            game.makeMove(proposedMove);

            System.out.println("Move successful: " + startPosition + " to " + targetPosition);
        } catch (InvalidMoveException ime) {
            System.out.println("Invalid move: " + ime.getMessage());
        } catch (Exception e) {
            System.out.println("An error occurred: " + e.getMessage());
        }
    }

    private ChessPosition promptForPosition() {
        System.out.print("Column (a-h): ");
        String colLetter = scanner.nextLine().trim().toLowerCase();
        if (colLetter.isEmpty() || colLetter.charAt(0) < 'a' || colLetter.charAt(0) > 'h') {
            System.out.println("Invalid column input. Please enter a letter from a to h.");
            return null;
        }
        int col = colLetter.charAt(0) - 'a' + 1;

        System.out.print("Row (1-8): ");
        int row;
        try {
            row = scanner.nextInt();
            scanner.nextLine(); // Consume newline left-over
            if (row < 1 || row > 8) {
                System.out.println("Invalid row input. Please enter a number from 1 to 8.");
                return null;
            }
        } catch (InputMismatchException ex) {
            System.out.println("Invalid input. Please enter a number.");
            scanner.nextLine(); // Consume the invalid input
            return null;
        }

        return new ChessPosition(row, col);
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
// Display the board from both perspectives
    public void displayBoards(Set<ChessPosition> highlightPositions) {
        System.out.println("From White's Perspective:");
        displayBoardFromWhitePerspective(highlightPositions);
        System.out.println(); // Add a separator between the two boards
        System.out.println("From Black's Perspective:");
        displayBoardFromBlackPerspective(highlightPositions);
    }


    private void displayBoardFromWhitePerspective(Set<ChessPosition> highlightPositions) {
        System.out.println(WHITE_PERSPECTIVE_LETTERS);
        for (int row = 8; row >= 1; row--) {
            System.out.print(row + " ");
            for (char col = 'a'; col <= 'h'; col++) {
                printSquare(col, row, highlightPositions);
            }
            System.out.println(" " + row);
        }
        System.out.println(WHITE_PERSPECTIVE_LETTERS);
    }

    private void displayBoardFromBlackPerspective(Set<ChessPosition> highlightPositions) {
        System.out.println(BLACK_PERSPECTIVE_LETTERS);
        for (int row = 1; row <= 8; row++) {
            System.out.print(row + " ");
            for (char col = 'h'; col >= 'a'; col--) {
                printSquare(col, row, highlightPositions);
            }
            System.out.println(" " + row);
        }
        System.out.println(BLACK_PERSPECTIVE_LETTERS);
    }

    private void printSquare(char col, int row, Set<ChessPosition> highlightPositions) {
        int colIndex = col - 'a' + 1;
        ChessPosition position = new ChessPosition(row, colIndex);
        ChessPiece piece = game.getBoard().getPiece(position);

        boolean isHighlight = highlightPositions.contains(position);
        String backgroundColor = determineBackgroundColor(row, colIndex, isHighlight);

        String pieceSymbol = (piece != null) ? getUnicodeSymbol(piece) : EM_SPACE;
        System.out.print(backgroundColor + " " + pieceSymbol + " " + ANSI_RESET);

    }


    private String determineBackgroundColor(int row, int colIndex, boolean isHighlight) {
        boolean isWhiteSquare = (row + colIndex) % 2 == 0;
        if (isHighlight) {
            return ANSI_GREEN;  // Shows up empty, but it's kinda highlighted I guess;
        }
        return isWhiteSquare ? ANSI_WHITE_BACKGROUND : ANSI_BLACK_BACKGROUND;
    }

    private void sendWebSocketMessage(String message) {
        if (wsClient != null && wsClient.isConnected()) {
            wsClient.sendMessage(message);
        } else {
            System.out.println("WebSocket connection is not open. Attempting to reconnect...");
            wsClient.connect();  // Attempt to reconnect if not connected
            if (wsClient.isConnected()) {
                wsClient.sendMessage(message);  // Resend message after reconnecting
            } else {
                System.out.println("Reconnection failed. Message not sent: " + message);
            }
        }
    }



    private String getUnicodeSymbol(ChessPiece piece) {
        if (piece.getTeamColor() == ChessGame.TeamColor.WHITE) {
            return whitePieceSymbols.get(piece.getPieceType());
        } else {
            return blackPieceSymbols.get(piece.getPieceType());
        }
    }

}
