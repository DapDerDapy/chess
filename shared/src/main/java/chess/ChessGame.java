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

        // Intialize parameter startPosition
        ChessPiece piece = grid.getPiece(startPosition);
        Collection<ChessMove> potentialMoves = piece.pieceMoves(grid, startPosition);

        // Filter out invalid moves
        for (ChessMove move : potentialMoves) {
            // Perform the check here. At a single move with the given start position and grid, would that move
            // put the king in danger? However, if the piece can move into a position where it can destroy the
            // piece that endangers the king, that is okay. But if there's another piece that can get the king
            // then it cannot go there.

            if (isMoveValid(move, grid, startPosition)) {
                moves.add(move);
            }
        }
        return moves;
    }

    private boolean isMoveValid(ChessMove move, ChessBoard board, ChessPosition startPosition){

        // Say, the bishop moves. Every possible place where it thinks it can move, it should check every single
        // other enemy piece and see if it can attack the king, if it can, that move is NOT valid!

        // maybe it should just see if any piece can hit the king of that piece moves at all.

        // It should check the moves from every potential move from every piece.

        Collection<ChessMove> opponentMoves = new HashSet<>();

        ChessPosition startPiecePosition = new ChessPosition(startPosition.getRow(), startPosition.getColumn());
        ChessPiece startPiece = board.getPiece(startPiecePosition);

        // hypothetically.... say the piece moved from its position?
        board.addPiece(startPosition, null);
        board.addPiece(move.getEndPosition(), startPiece);

        //let's find the king and get its position

        for(int i = 1; i <= 8; i++){
            for (int j = 1; j <= 8; j++){
                ChessPosition possibleKingPosition = new ChessPosition(i, j);
                ChessPiece possiblyKingPiece = board.getPiece(possibleKingPosition);

                if (possiblyKingPiece.getPieceType() == ChessPiece.PieceType.KING &&
                    possiblyKingPiece.getTeamColor() == startPiece.getTeamColor()){

                    ChessPosition kingPosition = possibleKingPosition;
                    ChessPiece kingPiece = possiblyKingPiece;
                    break;
                }
            }
        }

        for (int i = 1; i <= 8; i++){
            for (int j = 1; j <= 8; j++){

                // Check this piece, is it a
                ChessPosition opponentPosition = new ChessPosition(i,j);
                ChessPiece opponentPiece = board.getPiece(opponentPosition);

                if (opponentPiece != null){
                    if (opponentPiece.getTeamColor() != startPiece.getTeamColor()){

                        //get all possible moves from that piece if that piece moved from its position

                        opponentMoves = opponentPiece.pieceMoves(board, new ChessPosition(i,j));

                        // 1st, check if any of those moves INCLUDE the king's position
                        // IF SO, return FALSE AFTER STEP three

                        // 2nd, check if the chess piece that WOULD theoretically check the king
                        // can take the opponent piece

                        // because the piece is null, create the temporary grid to have the piece in the
                        // new location!!!!!!!!!!!!!!!!!!! Then just check if the king is in danger.

                        // if the chessmove is the starting position of the piece that puts it in danger it's still
                        // a valid move as long as there isn't another.

                        if (move.getEndPosition() == opponentPosition){


                            // 3rd, check if taking that opponent piece STILL leaves the king exposed
                            // IF SO, return false
                        }


                        // return
                    }
                }
            }
        }




        return true;
    }

    /**
     * Makes a move in a chess game
     *
     * @param move chess move to preform
     * @throws InvalidMoveException if move is invalid
     */
    public void makeMove(ChessMove move) throws InvalidMoveException {
        throw new RuntimeException("Not implemented");
    }

    /**
     * Determines if the given team is in check
     *
     * @param teamColor which team to check for check
     * @return True if the specified team is in check
     */
    public boolean isInCheck(TeamColor teamColor) {

        // if, on the opponents turn, the king can be captured.

        return false;
    }

    /**
     * Determines if the given team is in checkmate
     *
     * @param teamColor which team to check for checkmate
     * @return True if the specified team is in checkmate
     */
    public boolean isInCheckmate(TeamColor teamColor) {

        //If King Valid moves == NULL

        return false;
    }

    /**
     * Determines if the given team is in stalemate, which here is defined as having
     * no valid moves
     *
     * @param teamColor which team to check for stalemate
     * @return True if the specified team is in stalemate, otherwise false
     */
    public boolean isInStalemate(TeamColor teamColor) {
        //if valid moves == NONE


        return false;
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
