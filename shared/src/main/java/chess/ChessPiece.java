package chess;

import java.sql.Array;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Objects;

/**
 * Represents a single chess piece
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessPiece {

    private final ChessGame.TeamColor pieceColor;
    private final PieceType type;


    public ChessPiece(ChessGame.TeamColor pieceColor, ChessPiece.PieceType type) {
        this.pieceColor = pieceColor;
        this.type = type;
    }

    /**
     * The various different chess piece options
     */
    public enum PieceType {
        KING,
        QUEEN,
        BISHOP,
        KNIGHT,
        ROOK,
        PAWN
    }

    /**
     * @return Which team this chess piece belongs to
     */
    public ChessGame.TeamColor getTeamColor() {
        return pieceColor;
        //throw new RuntimeException("Not implemented");
    }

    /**
     * @return which type of chess piece this piece is
     */
    public PieceType getPieceType() {
        return type;
        //throw new RuntimeException("Not implemented");
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ChessPiece that)) return false;
        return pieceColor == that.pieceColor && type == that.type;
    }

    @Override
    public int hashCode() {
        return Objects.hash(pieceColor, type);
    }

    /**
     * Calculates all the positions a chess piece can move to
     * Does not take into account moves that are illegal due to leaving the king in
     * danger
     *
     * @return Collection of valid moves
     */
    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition) {
        Collection<ChessMove> moves = new HashSet<>();

        if (type.equals(PieceType.BISHOP)) {

            // TOP RIGHT
            for (int i = myPosition.getRow() + 1, j = myPosition.getColumn() + 1; i <= 8 && j <= 8; i++, j++){

                ChessPosition newPosition = new ChessPosition(i, j);
                ChessPiece pieceAtNewPosition = board.getPiece(newPosition);

                if (pieceAtNewPosition == null){
                    System.out.println("{" + i + ", " + j + "}");
                    moves.add(new ChessMove(myPosition, new ChessPosition(i, j), PieceType.BISHOP));
                }
                else{
                    break;
                }
            }

            // BOTTOM RIGHT
            for (int i = myPosition.getRow() - 1, j = myPosition.getColumn() + 1; i >= 1 && j <= 8; i--, j++){


                ChessPosition newPosition = new ChessPosition(i, j);
                ChessPiece pieceAtNewPosition = board.getPiece(newPosition);

                if (pieceAtNewPosition == null){
                    System.out.println("{" + i + ", " + j + "}");
                    moves.add(new ChessMove(myPosition, new ChessPosition(i, j), PieceType.BISHOP));
                }
                else{
                    break;
                }
            }

            // BOTTOM LEFT
            for (int i = myPosition.getRow() - 1, j = myPosition.getColumn() - 1; i >= 1 && j >= 1; i--, j--){

                ChessPosition newPosition = new ChessPosition(i, j);
                ChessPiece pieceAtNewPosition = board.getPiece(newPosition);

                if (pieceAtNewPosition == null){
                    System.out.println("{" + i + ", " + j + "}");
                    moves.add(new ChessMove(myPosition, new ChessPosition(i, j), PieceType.BISHOP));
                }
                else{
                    break;
                }
            }

            // TOP LEFT
            for (int i = myPosition.getRow() + 1, j = myPosition.getColumn() - 1; i <= 8 && j >= 1; i++, j--){


                ChessPosition newPosition = new ChessPosition(i, j);
                ChessPiece pieceAtNewPosition = board.getPiece(newPosition);

                if (pieceAtNewPosition == null){
                    System.out.println("{" + i + ", " + j + "}");
                    moves.add(new ChessMove(myPosition, new ChessPosition(i, j), PieceType.BISHOP));
                }
                else{
                    break;
                }
            }

            System.out.println(moves.toString());
            return moves;
        }

        return new HashSet<>();
        //throw new RuntimeException("Not implemented");
    }
}
