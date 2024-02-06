package chess;

import java.util.Collection;
import java.util.HashSet;

/**
 * For a class that can manage a chess game, making moves on a board
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessGame {

    private TeamColor teamTurn;
    private ChessBoard grid;

    public ChessGame() {
        this.teamTurn = TeamColor.WHITE;
    }

    /**
     * @return Which team's turn it is
     */
    public TeamColor getTeamTurn() {
        return teamTurn;
    }

    /**
     * Set's which teams turn it is
     *
     * @param team the team whose turn it is
     */
    public void setTeamTurn(TeamColor team) {
        teamTurn = team;
    }

    /**
     * Enum identifying the 2 possible teams in a chess game
     */
    public enum TeamColor {
        WHITE,
        BLACK
    }

    /**
     * Gets a valid moves for a piece at the given location
     *
     * @param startPosition the piece to get valid moves for
     * @return Set of valid moves for requested piece, or null if no piece at
     * startPosition
     */
    public Collection<ChessMove> validMoves(ChessPosition startPosition) {

        // If there is nothing at the start position, return null
        Collection<ChessMove> moves = new HashSet<>();

        if (startPosition == null){
            return null;
        }

        // Initialize parameter startPosition
        ChessPiece piece = grid.getPiece(startPosition);
        Collection<ChessMove> potentialMoves = piece.pieceMoves(grid, startPosition);

        // Filter out invalid moves
        for (ChessMove move : potentialMoves) {
            if (isMoveValid(move, grid, startPosition)) {
                moves.add(move);
            }
        }
        return moves;
    }


    private boolean isMoveValid(ChessMove move, ChessBoard board, ChessPosition startPosition){

        Collection<ChessMove> opponentMoves;

        ChessPosition startPiecePosition = new ChessPosition(startPosition.getRow(), startPosition.getColumn());
        ChessPiece startPiece = board.getPiece(startPiecePosition);

        // hypothetically.... say the piece moved from its position?
        board.addPiece(startPosition, null);
        board.addPiece(move.getEndPosition(), startPiece);

        //let's find the king and get its position
        ChessPosition kingPosition = findKingPosition(board, startPiece.getTeamColor());

        // Special Bool for finding things
        boolean takeDangerPiece = false;

        for (int i = 1; i <= 8; i++) {
            for (int j = 1; j <= 8; j++) {
                ChessPosition opponentPosition = new ChessPosition(i, j);
                ChessPiece opponentPiece = board.getPiece(opponentPosition);

                // If it's an opponent's piece.
                if (opponentPiece != null && opponentPiece.getTeamColor() != startPiece.getTeamColor()) {
                    opponentMoves = opponentPiece.pieceMoves(board, new ChessPosition(i, j));

                    // Check if any of the opponent's moves target the king's position.
                    for (ChessMove oppMove : opponentMoves) {
                        if (oppMove.getEndPosition().equals(kingPosition)) {
                            if (move.getEndPosition().equals(opponentPosition)){
                                // Not always going to be true
                                //takeDangerPiece = true;
                                return true;
                            }
                            // Undo the move before returning false.
                            board.addPiece(startPosition, startPiece); // Move back the start piece.
                            board.addPiece(move.getEndPosition(), null); // Clear the end position.
                            return false; // The move is not valid as it puts the king in check.
                        }
                    }
                }
            }
        }

        return true;

        // Okay, so it shouldn't return prematurely
    }

    private ChessPosition findKingPosition(ChessBoard board, TeamColor teamColor) {
        for (int i = 1; i <= 8; i++) {
            for (int j = 1; j <= 8; j++) {
                ChessPosition position = new ChessPosition(i, j);
                ChessPiece piece = board.getPiece(position);
                if (piece != null && piece.getPieceType() == ChessPiece.PieceType.KING &&
                                     piece.getTeamColor() == teamColor) {
                    return position;
                }
            }
        }
        return null; // Or throw an exception if the king must exist
    }

    /**
     * Makes a move in a chess game
     *
     * @param move chess move to preform
     * @throws InvalidMoveException if move is invalid
     */
    public void makeMove(ChessMove move) throws InvalidMoveException {
        ChessPiece movingPiece = grid.getPiece(move.getStartPosition());
        if (movingPiece == null) {
            throw new InvalidMoveException("No piece at the starting position.");
        }

        // Check if the move is among the legal moves for the piece
        Collection<ChessMove> legalMoves = movingPiece.pieceMoves(grid, move.getStartPosition());
        if (!legalMoves.contains(move)) {
            throw new InvalidMoveException("Move is not legal for the piece.");
        }

        // Check to make sure that it's the piece's turn
        //if (getTeamTurn() != movingPiece.getTeamColor()){
        //    throw new InvalidMoveException("Not piece's turn.");
        //}

        // Simulate the move to check if it puts your own king in check
        ChessPiece capturedPiece = grid.getPiece(move.getEndPosition()); // Save the captured piece if any
        grid.addPiece(move.getEndPosition(), movingPiece); // Move the piece to the new position
        grid.addPiece(move.getStartPosition(), null); // Remove the piece from the original position

        if (isInCheck(movingPiece.getTeamColor())) {
            // Undo the move if it puts your own king in check
            grid.addPiece(move.getStartPosition(), movingPiece);
            grid.addPiece(move.getEndPosition(), capturedPiece);
            throw new InvalidMoveException("Move would put or leave your king in check.");
        }

        // After making the move, check for checkmate or stalemate against the opponent
        TeamColor opponentColor = (movingPiece.getTeamColor() == TeamColor.WHITE) ? TeamColor.BLACK : TeamColor.WHITE;
        if (isInCheckmate(opponentColor)) {
            // Handle the checkmate scenario
            // This could involve setting some state to end the game or notifying the players
        } else if (isInStalemate(opponentColor)) {
            // Handle the stalemate scenario
            // Similar to checkmate, adjust the game state or notify players as necessary
        }

        // Optionally, here you would also switch turns between players
        //throw new InvalidMoveException("Move would put or leave your king in check.");
    }

    /**
     * Determines if the given team is in check
     *
     * @param teamColor which team to check for check
     * @return True if the specified team is in check
     */
    public boolean isInCheck(TeamColor teamColor) {
        Collection<ChessMove> opponentMoves;
        ChessPosition kingPosition = findKingPosition(grid, teamColor);
        // if, on the opponents turn, the king can be captured.
            for (int i = 1; i <= 8; i++) {
                for (int j = 1; j <= 8; j++) {
                    ChessPosition opponentPosition = new ChessPosition(i, j);
                    ChessPiece opponentPiece = grid.getPiece(opponentPosition);

                    if (opponentPiece != null && opponentPiece.getTeamColor() != teamColor){
                        opponentMoves = opponentPiece.pieceMoves(grid, new ChessPosition(i, j));

                        for (ChessMove oppMove : opponentMoves) {
                            if (oppMove.getEndPosition().equals(kingPosition)) {
                                return true;
                            }
                        }
                    }
                }
            }
        return false;
    }

    /**
     * Determines if the given team is in checkmate
     *
     * @param teamColor which team to check for checkmate
     * @return True if the specified team is in checkmate
     */
    public boolean isInCheckmate(TeamColor teamColor) {
        // First, check if the king is in check.
        if (!isInCheck(teamColor)) {
            return false; // Not in checkmate if the king is not in check.
        }

        // Attempt to find a legal move that would get the king out of check.
        for (int i = 1; i <= 8; i++) {
            for (int j = 1; j <= 8; j++) {
                ChessPosition position = new ChessPosition(i, j);
                ChessPiece piece = grid.getPiece(position);

                // Consider only the pieces of the team currently in check.
                if (piece != null && piece.getTeamColor() == teamColor) {
                    Collection<ChessMove> possibleMoves = piece.pieceMoves(grid, position);

                    for (ChessMove move : possibleMoves) {
                        // Simulate each move.
                        ChessPiece targetPiece = grid.getPiece(move.getEndPosition());
                        grid.addPiece(move.getEndPosition(), piece); // Make the move
                        grid.addPiece(position, null); // Remove the piece from the original position

                        boolean stillInCheck = isInCheck(teamColor);

                        // Undo the move.
                        grid.addPiece(position, piece);
                        grid.addPiece(move.getEndPosition(), targetPiece);

                        if (!stillInCheck) {
                            return false; // Found a move that gets the king out of check, so it's not checkmate.
                        }
                    }
                }
            }
        }

        // If no legal moves can remove the king from check, it's checkmate.
        return true;
    }


    /**
     * Determines if the given team is in stalemate, which here is defined as having
     * no valid moves
     *
     * @param teamColor which team to check for stalemate
     * @return True if the specified team is in stalemate, otherwise false
     */
    public boolean isInStalemate(TeamColor teamColor) {
        boolean hasLegalMove = false;

        for (int i = 1; i <= 8; i++) {
            for (int j = 1; j <= 8; j++) {
                ChessPosition position = new ChessPosition(i, j);
                ChessPiece piece = grid.getPiece(position);

                if (piece != null && piece.getTeamColor() == teamColor) {
                    Collection<ChessMove> possibleMoves = piece.pieceMoves(grid, position);

                    for (ChessMove move : possibleMoves) {
                        // Temporarily make the move on the board
                        ChessPiece targetPiece = grid.getPiece(move.getEndPosition());
                        grid.addPiece(move.getEndPosition(), piece);
                        grid.addPiece(position, null);

                        // Check if making this move puts the king in check
                        if (!isInCheck(teamColor)) {
                            hasLegalMove = true; // Found at least one legal move
                        }

                        // Undo the move
                        grid.addPiece(position, piece);
                        grid.addPiece(move.getEndPosition(), targetPiece);

                        if (hasLegalMove) {
                            return false; // Not a stalemate, since there's at least one legal move
                        }
                    }
                }
            }
        }

        // If no legal moves were found and the king is not in check, it's a stalemate
        return !isInCheck(teamColor);
    }

    /**
     * Sets this game's chessboard with a given board
     *
     * @param board the new board to use
     */
    public void setBoard(ChessBoard board) {
        grid = board;
    }

    /**
     * Gets the current chessboard
     *
     * @return the chessboard
     */
    public ChessBoard getBoard() {
        return grid;
    }
}
