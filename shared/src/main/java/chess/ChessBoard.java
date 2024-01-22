package chess;

import java.util.Arrays;

/**
 * A chessboard that can hold and rearrange chess pieces.
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessBoard {


    private final ChessPiece[][] grid = new ChessPiece[9][9];
    public ChessBoard() {

    }

    /**
     * Adds a chess piece to the chessboard
     *
     * @param position where to add the piece to
     * @param piece    the piece to add
     */
    public void addPiece(ChessPosition position, ChessPiece piece) {
        grid[position.getRow()][position.getColumn()] = piece;
    }

    /**
     * Gets a chess piece on the chessboard
     *
     * @param position The position to get the piece from
     * @return Either the piece at the position, or null if no piece is at that
     * position
     */
    public ChessPiece getPiece(ChessPosition position) {
        return grid[position.getRow()][position.getColumn()];
    }


    /**
     * Sets the board to the default starting board
     * (How the game of chess normally starts)
     */
    public void resetBoard() {

        for(int i = 0; i <= 8; i++){
            for (int j = 0; j <= 8; j++){
                grid[i][j] = null;
            }
        }

        ChessPosition newPosition;
        ChessPiece newPiece;

        // Add pawns to white
        for (int i = 1; i <= 8; i++){
            newPosition = new ChessPosition(2, i);
            newPiece = new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.PAWN);
            addPiece(newPosition, newPiece);
        }

        // Add pawns to black
        for (int i = 1; i <= 8; i++){
            newPosition = new ChessPosition(7, i);
            newPiece = new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.PAWN);
            addPiece(newPosition, newPiece);
        }

        // Add White Rooks
        newPosition = new ChessPosition(1,1);
        newPiece = new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.ROOK);
        addPiece(newPosition, newPiece);

        newPosition = new ChessPosition(1,8);
        newPiece = new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.ROOK);
        addPiece(newPosition, newPiece);

        // Add Black Rooks
        newPosition = new ChessPosition(8,1);
        newPiece = new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.ROOK);
        addPiece(newPosition, newPiece);

        newPosition = new ChessPosition(8,8);
        newPiece = new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.ROOK);
        addPiece(newPosition, newPiece);

        // Add White Knights
        newPosition = new ChessPosition(1,2);
        newPiece = new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.KNIGHT);
        addPiece(newPosition, newPiece);

        newPosition = new ChessPosition(1,7);
        newPiece = new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.KNIGHT);
        addPiece(newPosition, newPiece);

        // Add Black Knights
        newPosition = new ChessPosition(8,2);
        newPiece = new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.KNIGHT);
        addPiece(newPosition, newPiece);

        newPosition = new ChessPosition(8,7);
        newPiece = new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.KNIGHT);
        addPiece(newPosition, newPiece);


        // Add White Bishops
        newPosition = new ChessPosition(1,3);
        newPiece = new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.BISHOP);
        addPiece(newPosition, newPiece);

        newPosition = new ChessPosition(1,6);
        newPiece = new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.BISHOP);
        addPiece(newPosition, newPiece);

        // Add Black Bishops
        newPosition = new ChessPosition(8,3);
        newPiece = new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.BISHOP);
        addPiece(newPosition, newPiece);

        newPosition = new ChessPosition(8,6);
        newPiece = new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.BISHOP);
        addPiece(newPosition, newPiece);


        // Add White Queen
        newPosition = new ChessPosition(1,4);
        newPiece = new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.QUEEN);
        addPiece(newPosition, newPiece);

        // Add Black Queen
        newPosition = new ChessPosition(8,4);
        newPiece = new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.QUEEN);
        addPiece(newPosition, newPiece);

        // Add White King
        newPosition = new ChessPosition(1,5);
        newPiece = new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.KING);
        addPiece(newPosition, newPiece);

        // Add Black King
        newPosition = new ChessPosition(8,5);
        newPiece = new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.KING);
        addPiece(newPosition, newPiece);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ChessBoard that)) return false;
        return Arrays.deepEquals(grid, that.grid);
    }

    @Override
    public int hashCode() {
        return Arrays.deepHashCode(grid);
    }
}
