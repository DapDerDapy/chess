package chess;

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

        ChessPosition newPosition;
        ChessPiece pieceAtNewPosition;

        if (type.equals(PieceType.BISHOP)) {

            // TOP RIGHT
            for (int i = myPosition.getRow() + 1, j = myPosition.getColumn() + 1; i <= 8 && j <= 8; i++, j++){

                newPosition = new ChessPosition(i, j);
                pieceAtNewPosition = board.getPiece(newPosition);

                if (pieceAtNewPosition == null){
                    moves.add(new ChessMove(myPosition, new ChessPosition(i, j),null));

                } else if (pieceAtNewPosition.pieceColor != this.pieceColor){
                    moves.add(new ChessMove(myPosition, new ChessPosition(i, j), null));
                    break;

                } else{
                    break;
                }
            }

            // BOTTOM RIGHT
            for (int i = myPosition.getRow() - 1, j = myPosition.getColumn() + 1; i >= 1 && j <= 8; i--, j++){

                newPosition = new ChessPosition(i, j);
                pieceAtNewPosition = board.getPiece(newPosition);

                if (pieceAtNewPosition == null){
                    moves.add(new ChessMove(myPosition, new ChessPosition(i, j),null));

                } else if (pieceAtNewPosition.pieceColor != this.pieceColor){
                    moves.add(new ChessMove(myPosition, new ChessPosition(i, j), null));
                    break;

                } else{
                    break;
                }
            }

            // BOTTOM LEFT
            for (int i = myPosition.getRow() - 1, j = myPosition.getColumn() - 1; i >= 1 && j >= 1; i--, j--){

                newPosition = new ChessPosition(i, j);
                pieceAtNewPosition = board.getPiece(newPosition);

                if (pieceAtNewPosition == null){
                    moves.add(new ChessMove(myPosition, new ChessPosition(i, j), null));

                } else if (pieceAtNewPosition.pieceColor != this.pieceColor){
                    moves.add(new ChessMove(myPosition, new ChessPosition(i, j), null));
                    break;

                } else{
                    break;
                }
            }

            // TOP LEFT
            for (int i = myPosition.getRow() + 1, j = myPosition.getColumn() - 1; i <= 8 && j >= 1; i++, j--){

                newPosition = new ChessPosition(i, j);
                pieceAtNewPosition = board.getPiece(newPosition);

                if (pieceAtNewPosition == null){
                    moves.add(new ChessMove(myPosition, new ChessPosition(i, j), null));

                } else if (pieceAtNewPosition.pieceColor != this.pieceColor){
                    moves.add(new ChessMove(myPosition, new ChessPosition(i, j), null));
                    break;

                } else{
                    break;
                }
            }
            return moves;
        }

        if (type.equals(PieceType.KNIGHT)) {
            int knightRow;
            int knightCol;

            // UP 2 RIGHT 1
            knightRow = myPosition.getRow() + 2;
            knightCol = myPosition.getColumn() + 1;

            if (knightRow <= 8 && knightCol <= 8 && knightRow >= 1 && knightCol >= 1){
                newPosition = new ChessPosition(knightRow, knightCol);
                pieceAtNewPosition = board.getPiece(newPosition);


                if (pieceAtNewPosition == null || pieceAtNewPosition.pieceColor != this.pieceColor){
                    moves.add(new ChessMove(myPosition, new ChessPosition(knightRow, knightCol), null));
                }
            }

            // UP 2 LEFT 1
            knightRow = myPosition.getRow() + 2;
            knightCol = myPosition.getColumn() - 1;

            if (knightRow <= 8 && knightCol <= 8 && knightRow >= 1 && knightCol >= 1){
                newPosition = new ChessPosition(knightRow, knightCol);
                pieceAtNewPosition = board.getPiece(newPosition);

                if (pieceAtNewPosition == null || pieceAtNewPosition.pieceColor != this.pieceColor){
                    moves.add(new ChessMove(myPosition, new ChessPosition(knightRow, knightCol), null));
                }
            }

            // RIGHT 2 UP 1
            knightRow = myPosition.getRow() + 1;
            knightCol = myPosition.getColumn() + 2;

            if (knightRow <= 8 && knightCol <= 8 && knightRow >= 1 && knightCol >= 1){
                newPosition = new ChessPosition(knightRow, knightCol);
                pieceAtNewPosition = board.getPiece(newPosition);

                if (pieceAtNewPosition == null || pieceAtNewPosition.pieceColor != this.pieceColor){
                    moves.add(new ChessMove(myPosition, new ChessPosition(knightRow, knightCol), null));
                }
            }

            // RIGHT 2 DOWN 1
            knightRow = myPosition.getRow() - 1;
            knightCol = myPosition.getColumn() + 2;

            if (knightRow <= 8 && knightCol <= 8 && knightRow >= 1 && knightCol >= 1){
                newPosition = new ChessPosition(knightRow, knightCol);
                pieceAtNewPosition = board.getPiece(newPosition);

                if (pieceAtNewPosition == null || pieceAtNewPosition.pieceColor != this.pieceColor){
                    moves.add(new ChessMove(myPosition, new ChessPosition(knightRow, knightCol), null));
                }
            }

            // DOWN 2 RIGHT 1
            knightRow = myPosition.getRow() - 2;
            knightCol = myPosition.getColumn() + 1;

            if (knightRow <= 8 && knightCol <= 8 && knightRow >= 1 && knightCol >= 1){
                newPosition = new ChessPosition(knightRow, knightCol);
                pieceAtNewPosition = board.getPiece(newPosition);

                if (pieceAtNewPosition == null || pieceAtNewPosition.pieceColor != this.pieceColor){
                    moves.add(new ChessMove(myPosition, new ChessPosition(knightRow, knightCol), null));
                }
            }


            // DOWN 2 LEFT 1
            knightRow = myPosition.getRow() - 2;
            knightCol = myPosition.getColumn() - 1;

            if (knightRow <= 8 && knightCol <= 8 && knightRow >= 1 && knightCol >= 1){
                newPosition = new ChessPosition(knightRow, knightCol);
                pieceAtNewPosition = board.getPiece(newPosition);

                if (pieceAtNewPosition == null || pieceAtNewPosition.pieceColor != this.pieceColor){
                    moves.add(new ChessMove(myPosition, new ChessPosition(knightRow, knightCol), null));
                }
            }

            // LEFT 2 DOWN 1
            knightRow = myPosition.getRow() - 1;
            knightCol = myPosition.getColumn() - 2;

            if (knightRow <= 8 && knightCol <= 8 && knightRow >= 1 && knightCol >= 1){
                newPosition = new ChessPosition(knightRow, knightCol);
                pieceAtNewPosition = board.getPiece(newPosition);

                if (pieceAtNewPosition == null || pieceAtNewPosition.pieceColor != this.pieceColor){
                    moves.add(new ChessMove(myPosition, new ChessPosition(knightRow, knightCol), null));
                }
            }

            // LEFT 2 UP 1
            knightRow = myPosition.getRow() + 1;
            knightCol = myPosition.getColumn() - 2;

            if (knightRow <= 8 && knightCol <= 8 && knightRow >= 1 && knightCol >= 1){
                newPosition = new ChessPosition(knightRow, knightCol);
                pieceAtNewPosition = board.getPiece(newPosition);

                if (pieceAtNewPosition == null || pieceAtNewPosition.pieceColor != this.pieceColor){
                    moves.add(new ChessMove(myPosition, new ChessPosition(knightRow, knightCol), null));
                }
            }

            return moves;
        }

        if (type.equals(PieceType.ROOK)){
            // for the CALCULATE ROOK MOVES CLASS, make 2 functions that do the math,
            // 1 for the column, and 1 for the row being what's staying put.

            // UP
            for (int i = myPosition.getRow() + 1; i <= 8; i++){
                newPosition = new ChessPosition(i, myPosition.getColumn());
                pieceAtNewPosition = board.getPiece(newPosition);

                if (pieceAtNewPosition == null){
                    moves.add(new ChessMove(myPosition, new ChessPosition(i, myPosition.getColumn()),null));

                } else if (pieceAtNewPosition.pieceColor != this.pieceColor){
                    moves.add(new ChessMove(myPosition, new ChessPosition(i, myPosition.getColumn()), null));
                    break;

                } else{
                    break;
                }
            }

            // DOWN
            for (int i = myPosition.getRow() - 1; i >= 1; i--){
                newPosition = new ChessPosition(i, myPosition.getColumn());
                pieceAtNewPosition = board.getPiece(newPosition);

                if (pieceAtNewPosition == null){
                    moves.add(new ChessMove(myPosition, new ChessPosition(i, myPosition.getColumn()), null));

                } else if (pieceAtNewPosition.pieceColor != this.pieceColor){
                    moves.add(new ChessMove(myPosition, new ChessPosition(i, myPosition.getColumn()), null));
                    break;

                } else{
                    break;
                }
            }

            // RIGHT
            for (int i = myPosition.getColumn() + 1; i <= 8; i++){
                newPosition = new ChessPosition(myPosition.getRow(), i);
                pieceAtNewPosition = board.getPiece(newPosition);

                if (pieceAtNewPosition == null){
                    moves.add(new ChessMove(myPosition, new ChessPosition(myPosition.getRow(), i), null));

                } else if (pieceAtNewPosition.pieceColor != this.pieceColor){
                    moves.add(new ChessMove(myPosition, new ChessPosition(myPosition.getRow(), i), null));
                    break;

                } else{
                    break;
                }
            }

            // LEFT
            for (int i = myPosition.getColumn() - 1; i >= 1; i--){
                newPosition = new ChessPosition(myPosition.getRow(), i);
                pieceAtNewPosition = board.getPiece(newPosition);

                if (pieceAtNewPosition == null){
                    moves.add(new ChessMove(myPosition, new ChessPosition(myPosition.getRow(), i), null));

                } else if (pieceAtNewPosition.pieceColor != this.pieceColor){
                    moves.add(new ChessMove(myPosition, new ChessPosition(myPosition.getRow(), i), null));
                    break;

                } else{
                    break;
                }
            }
            return moves;
        }

        if (type.equals(PieceType.QUEEN)){
            // essentially copy the copy all the code from bishop and from rook

            // UP
            for (int i = myPosition.getRow() + 1; i <= 8; i++){
                newPosition = new ChessPosition(i, myPosition.getColumn());
                pieceAtNewPosition = board.getPiece(newPosition);

                if (pieceAtNewPosition == null){
                    moves.add(new ChessMove(myPosition, new ChessPosition(i, myPosition.getColumn()), null));

                } else if (pieceAtNewPosition.pieceColor != this.pieceColor){
                    moves.add(new ChessMove(myPosition, new ChessPosition(i, myPosition.getColumn()), null));
                    break;

                } else{
                    break;
                }
            }

            // DOWN
            for (int i = myPosition.getRow() - 1; i >= 1; i--){
                newPosition = new ChessPosition(i, myPosition.getColumn());
                pieceAtNewPosition = board.getPiece(newPosition);

                if (pieceAtNewPosition == null){
                    moves.add(new ChessMove(myPosition, new ChessPosition(i, myPosition.getColumn()), null));

                } else if (pieceAtNewPosition.pieceColor != this.pieceColor){
                    moves.add(new ChessMove(myPosition, new ChessPosition(i, myPosition.getColumn()), null));
                    break;

                } else{
                    break;
                }
            }

            // RIGHT
            for (int i = myPosition.getColumn() + 1; i <= 8; i++){
                newPosition = new ChessPosition(myPosition.getRow(), i);
                pieceAtNewPosition = board.getPiece(newPosition);

                if (pieceAtNewPosition == null){
                    moves.add(new ChessMove(myPosition, new ChessPosition(myPosition.getRow(), i), null));

                } else if (pieceAtNewPosition.pieceColor != this.pieceColor){
                    moves.add(new ChessMove(myPosition, new ChessPosition(myPosition.getRow(), i), null));
                    break;

                } else{
                    break;
                }
            }

            // LEFT
            for (int i = myPosition.getColumn() - 1; i >= 1; i--){
                newPosition = new ChessPosition(myPosition.getRow(), i);
                pieceAtNewPosition = board.getPiece(newPosition);

                if (pieceAtNewPosition == null){
                    moves.add(new ChessMove(myPosition, new ChessPosition(myPosition.getRow(), i), null));

                } else if (pieceAtNewPosition.pieceColor != this.pieceColor){
                    moves.add(new ChessMove(myPosition, new ChessPosition(myPosition.getRow(), i), null));
                    break;

                } else{
                    break;
                }
            }

            // TOP RIGHT
            for (int i = myPosition.getRow() + 1, j = myPosition.getColumn() + 1; i <= 8 && j <= 8; i++, j++){

                newPosition = new ChessPosition(i, j);
                pieceAtNewPosition = board.getPiece(newPosition);

                if (pieceAtNewPosition == null){
                    moves.add(new ChessMove(myPosition, new ChessPosition(i, j), null));

                } else if (pieceAtNewPosition.pieceColor != this.pieceColor){
                    moves.add(new ChessMove(myPosition, new ChessPosition(i, j), null));
                    break;

                } else{
                    break;
                }
            }

            // BOTTOM RIGHT
            for (int i = myPosition.getRow() - 1, j = myPosition.getColumn() + 1; i >= 1 && j <= 8; i--, j++){

                newPosition = new ChessPosition(i, j);
                pieceAtNewPosition = board.getPiece(newPosition);

                if (pieceAtNewPosition == null){
                    moves.add(new ChessMove(myPosition, new ChessPosition(i, j), null));

                } else if (pieceAtNewPosition.pieceColor != this.pieceColor){
                    moves.add(new ChessMove(myPosition, new ChessPosition(i, j), null));
                    break;

                } else{
                    break;
                }
            }

            // BOTTOM LEFT
            for (int i = myPosition.getRow() - 1, j = myPosition.getColumn() - 1; i >= 1 && j >= 1; i--, j--){

                newPosition = new ChessPosition(i, j);
                pieceAtNewPosition = board.getPiece(newPosition);

                if (pieceAtNewPosition == null){
                    moves.add(new ChessMove(myPosition, new ChessPosition(i, j), null));

                } else if (pieceAtNewPosition.pieceColor != this.pieceColor){
                    moves.add(new ChessMove(myPosition, new ChessPosition(i, j), null));
                    break;

                } else{
                    break;
                }
            }

            // TOP LEFT
            for (int i = myPosition.getRow() + 1, j = myPosition.getColumn() - 1; i <= 8 && j >= 1; i++, j--){

                newPosition = new ChessPosition(i, j);
                pieceAtNewPosition = board.getPiece(newPosition);

                if (pieceAtNewPosition == null){
                    moves.add(new ChessMove(myPosition, new ChessPosition(i, j), null));

                } else if (pieceAtNewPosition.pieceColor != this.pieceColor){
                    moves.add(new ChessMove(myPosition, new ChessPosition(i, j), null));
                    break;

                } else{
                    break;
                }
            }
            return moves;
        }

        if (type.equals(PieceType.KING)){
            int kingRow;
            int kingCol;


            // UP
            kingRow = myPosition.getRow() + 1;
            kingCol = myPosition.getColumn();

            if (kingRow <= 8 && kingRow >= 1 && kingCol <= 8 && kingCol >= 1){
                newPosition = new ChessPosition(kingRow, kingCol);
                pieceAtNewPosition = board.getPiece(newPosition);

                if (pieceAtNewPosition == null || pieceAtNewPosition.pieceColor != this.pieceColor){
                    moves.add(new ChessMove(myPosition, new ChessPosition(kingRow, kingCol), null));
                }
            }

            // UP RIGHT
            kingRow = myPosition.getRow() + 1;
            kingCol = myPosition.getColumn() + 1;

            if (kingRow <= 8 && kingRow >= 1 && kingCol <= 8 && kingCol >= 1){
                newPosition = new ChessPosition(kingRow, kingCol);
                pieceAtNewPosition = board.getPiece(newPosition);

                if (pieceAtNewPosition == null || pieceAtNewPosition.pieceColor != this.pieceColor){
                    moves.add(new ChessMove(myPosition, new ChessPosition(kingRow, kingCol), null));
                }
            }

            // RIGHT
            kingRow = myPosition.getRow();
            kingCol = myPosition.getColumn() + 1;

            if (kingRow <= 8 && kingRow >= 1 && kingCol <= 8 && kingCol >= 1){
                newPosition = new ChessPosition(kingRow, kingCol);
                pieceAtNewPosition = board.getPiece(newPosition);

                if (pieceAtNewPosition == null || pieceAtNewPosition.pieceColor != this.pieceColor){
                    moves.add(new ChessMove(myPosition, new ChessPosition(kingRow, kingCol), null));
                }
            }

            // RIGHT DOWN
            kingRow = myPosition.getRow() - 1;
            kingCol = myPosition.getColumn() + 1;

            if (kingRow <= 8 && kingRow >= 1 && kingCol <= 8 && kingCol >= 1){
                newPosition = new ChessPosition(kingRow, kingCol);
                pieceAtNewPosition = board.getPiece(newPosition);

                if (pieceAtNewPosition == null || pieceAtNewPosition.pieceColor != this.pieceColor){
                    moves.add(new ChessMove(myPosition, new ChessPosition(kingRow, kingCol), null));
                }
            }

            // DOWN
            kingRow = myPosition.getRow() - 1;
            kingCol = myPosition.getColumn();


            if (kingRow <= 8 && kingRow >= 1 && kingCol <= 8 && kingCol >= 1){
                newPosition = new ChessPosition(kingRow, kingCol);
                pieceAtNewPosition = board.getPiece(newPosition);

                if (pieceAtNewPosition == null || pieceAtNewPosition.pieceColor != this.pieceColor){
                    moves.add(new ChessMove(myPosition, new ChessPosition(kingRow, kingCol), null));
                }
            }

            // DOWN LEFT
            kingRow = myPosition.getRow() - 1;
            kingCol = myPosition.getColumn() - 1;

            if (kingRow <= 8 && kingRow >= 1 && kingCol <= 8 && kingCol >= 1){
                newPosition = new ChessPosition(kingRow, kingCol);
                pieceAtNewPosition = board.getPiece(newPosition);

                if (pieceAtNewPosition == null || pieceAtNewPosition.pieceColor != this.pieceColor){
                    moves.add(new ChessMove(myPosition, new ChessPosition(kingRow, kingCol),null));
                }
            }

            //LEFT
            kingRow = myPosition.getRow();
            kingCol = myPosition.getColumn() - 1;

            if (kingRow <= 8 && kingRow >= 1 && kingCol <= 8 && kingCol >= 1){
                newPosition = new ChessPosition(kingRow, kingCol);
                pieceAtNewPosition = board.getPiece(newPosition);

                if (pieceAtNewPosition == null || pieceAtNewPosition.pieceColor != this.pieceColor){
                    moves.add(new ChessMove(myPosition, new ChessPosition(kingRow, kingCol), null));
                }
            }

            //LEFT UP
            kingRow = myPosition.getRow() + 1;
            kingCol = myPosition.getColumn() - 1;

            if (kingRow <= 8 && kingRow >= 1 && kingCol <= 8 && kingCol >= 1){
                newPosition = new ChessPosition(kingRow, kingCol);
                pieceAtNewPosition = board.getPiece(newPosition);

                if (pieceAtNewPosition == null || pieceAtNewPosition.pieceColor != this.pieceColor){
                    moves.add(new ChessMove(myPosition, new ChessPosition(kingRow, kingCol), null));
                }
            }
            return moves;
        }

        if (type.equals(PieceType.PAWN)){

            int pawnRow;
            int pawnCol;

            if (this.pieceColor.equals(ChessGame.TeamColor.WHITE)){

                pawnRow = myPosition.getRow() + 1;
                pawnCol = myPosition.getColumn() + 1;

                if (pawnRow <= 8 && pawnRow >= 1 && pawnCol <= 8 && pawnCol >= 1){
                    newPosition = new ChessPosition(pawnRow, pawnCol);
                    pieceAtNewPosition = board.getPiece(newPosition);

                    if (pieceAtNewPosition != null){
                        if (pieceAtNewPosition.pieceColor != this.pieceColor && myPosition.getRow() != 7){
                            moves.add(new ChessMove(myPosition, new ChessPosition(pawnRow, pawnCol), null));
                        } else if (myPosition.getRow() == 7){
                            moves.add(new ChessMove(myPosition, new ChessPosition(pawnRow, pawnCol), ChessPiece.PieceType.ROOK));
                            moves.add(new ChessMove(myPosition, new ChessPosition(pawnRow, pawnCol), ChessPiece.PieceType.BISHOP));
                            moves.add(new ChessMove(myPosition, new ChessPosition(pawnRow, pawnCol), ChessPiece.PieceType.QUEEN));
                            moves.add(new ChessMove(myPosition, new ChessPosition(pawnRow, pawnCol), ChessPiece.PieceType.KNIGHT));
                        }
                    }
                }

                pawnRow = myPosition.getRow() + 1;
                pawnCol = myPosition.getColumn() - 1;

                if (pawnRow <= 8 && pawnRow >= 1 && pawnCol <= 8 && pawnCol >= 1){
                    newPosition = new ChessPosition(pawnRow, pawnCol);
                    pieceAtNewPosition = board.getPiece(newPosition);

                    if (pieceAtNewPosition != null){
                        if (pieceAtNewPosition.pieceColor != this.pieceColor && myPosition.getRow() != 7){
                            moves.add(new ChessMove(myPosition, new ChessPosition(pawnRow, pawnCol), null));
                        } else if (myPosition.getRow() == 7){
                            moves.add(new ChessMove(myPosition, new ChessPosition(pawnRow, pawnCol), ChessPiece.PieceType.ROOK));
                            moves.add(new ChessMove(myPosition, new ChessPosition(pawnRow, pawnCol), ChessPiece.PieceType.BISHOP));
                            moves.add(new ChessMove(myPosition, new ChessPosition(pawnRow, pawnCol), ChessPiece.PieceType.QUEEN));
                            moves.add(new ChessMove(myPosition, new ChessPosition(pawnRow, pawnCol), ChessPiece.PieceType.KNIGHT));
                        }
                    }
                }

                if(myPosition.getRow() == 2){
                    // initial move
                    pawnRow = myPosition.getRow() + 1;
                    pawnCol = myPosition.getColumn();

                    newPosition = new ChessPosition(pawnRow, pawnCol);
                    pieceAtNewPosition = board.getPiece(newPosition);

                    if (pieceAtNewPosition == null){
                        moves.add(new ChessMove(myPosition, new ChessPosition(pawnRow, pawnCol), null));

                        pawnRow = myPosition.getRow() + 2;

                        newPosition = new ChessPosition(pawnRow, pawnCol);
                        pieceAtNewPosition = board.getPiece(newPosition);

                        if (pieceAtNewPosition == null){
                            moves.add(new ChessMove(myPosition, new ChessPosition(pawnRow, pawnCol), null));
                        }
                    }


                } else {
                    pawnRow = myPosition.getRow() + 1;
                    pawnCol = myPosition.getColumn();

                    if (pawnRow <= 8 && pawnRow >= 1 && pawnCol <=8 && pawnCol >= 1){
                        newPosition = new ChessPosition(pawnRow, pawnCol);
                        pieceAtNewPosition = board.getPiece(newPosition);
                        if (pieceAtNewPosition == null && myPosition.getRow() !=7 ) {
                            moves.add(new ChessMove(myPosition, new ChessPosition(pawnRow, pawnCol), null));
                        } else if (pieceAtNewPosition == null && myPosition.getRow() == 7){
                            moves.add(new ChessMove(myPosition, new ChessPosition(pawnRow, pawnCol), ChessPiece.PieceType.ROOK));
                            moves.add(new ChessMove(myPosition, new ChessPosition(pawnRow, pawnCol), ChessPiece.PieceType.BISHOP));
                            moves.add(new ChessMove(myPosition, new ChessPosition(pawnRow, pawnCol), ChessPiece.PieceType.QUEEN));
                            moves.add(new ChessMove(myPosition, new ChessPosition(pawnRow, pawnCol), ChessPiece.PieceType.KNIGHT));
                        }
                    }
                }
            }

            if (this.pieceColor.equals(ChessGame.TeamColor.BLACK)){

                pawnRow = myPosition.getRow() - 1;
                pawnCol = myPosition.getColumn() - 1;


                if (pawnRow <= 8 && pawnRow >= 1 && pawnCol <= 8 && pawnCol >= 1){
                    newPosition = new ChessPosition(pawnRow, pawnCol);
                    pieceAtNewPosition = board.getPiece(newPosition);


                    if (pieceAtNewPosition != null){
                        if (pieceAtNewPosition.pieceColor != this.pieceColor && myPosition.getRow() != 2){
                            moves.add(new ChessMove(myPosition, new ChessPosition(pawnRow, pawnCol), null));
                        } else if (myPosition.getRow() == 2){
                            moves.add(new ChessMove(myPosition, new ChessPosition(pawnRow, pawnCol), ChessPiece.PieceType.ROOK));
                            moves.add(new ChessMove(myPosition, new ChessPosition(pawnRow, pawnCol), ChessPiece.PieceType.BISHOP));
                            moves.add(new ChessMove(myPosition, new ChessPosition(pawnRow, pawnCol), ChessPiece.PieceType.QUEEN));
                            moves.add(new ChessMove(myPosition, new ChessPosition(pawnRow, pawnCol), ChessPiece.PieceType.KNIGHT));
                        }
                    }
                }

                pawnRow = myPosition.getRow() - 1;
                pawnCol = myPosition.getColumn() + 1;

                if (pawnRow <= 8 && pawnRow >= 1 && pawnCol <= 8 && pawnCol >= 1){
                    newPosition = new ChessPosition(pawnRow, pawnCol);
                    pieceAtNewPosition = board.getPiece(newPosition);

                    if (pieceAtNewPosition != null){
                        if (pieceAtNewPosition.pieceColor != this.pieceColor && myPosition.getRow() != 7){
                            moves.add(new ChessMove(myPosition, new ChessPosition(pawnRow, pawnCol), null));
                        } else if (pawnRow == 2){
                            moves.add(new ChessMove(myPosition, new ChessPosition(pawnRow, pawnCol), ChessPiece.PieceType.ROOK));
                            moves.add(new ChessMove(myPosition, new ChessPosition(pawnRow, pawnCol), ChessPiece.PieceType.BISHOP));
                            moves.add(new ChessMove(myPosition, new ChessPosition(pawnRow, pawnCol), ChessPiece.PieceType.QUEEN));
                            moves.add(new ChessMove(myPosition, new ChessPosition(pawnRow, pawnCol), ChessPiece.PieceType.KNIGHT));
                        }
                    }
                }


                if(myPosition.getRow() == 7){
                    // initial move
                    pawnRow = myPosition.getRow() - 1;
                    pawnCol = myPosition.getColumn();

                    newPosition = new ChessPosition(pawnRow, pawnCol);
                    pieceAtNewPosition = board.getPiece(newPosition);

                    if (pieceAtNewPosition == null){
                        moves.add(new ChessMove(myPosition, new ChessPosition(pawnRow, pawnCol), null));

                        pawnRow = myPosition.getRow() - 2;

                        newPosition = new ChessPosition(pawnRow, pawnCol);
                        pieceAtNewPosition = board.getPiece(newPosition);

                        if (pieceAtNewPosition == null){
                            moves.add(new ChessMove(myPosition, new ChessPosition(pawnRow, pawnCol), null));
                        }
                    }


                }else {
                    pawnRow = myPosition.getRow() - 1;
                    pawnCol = myPosition.getColumn();

                    newPosition = new ChessPosition(pawnRow, pawnCol);
                    pieceAtNewPosition = board.getPiece(newPosition);

                    if (pieceAtNewPosition == null && myPosition.getRow() !=2 ) {
                        moves.add(new ChessMove(myPosition, new ChessPosition(pawnRow, pawnCol), null));
                    } else if (pieceAtNewPosition == null && myPosition.getRow() == 2){
                        moves.add(new ChessMove(myPosition, new ChessPosition(pawnRow, pawnCol), ChessPiece.PieceType.ROOK));
                        moves.add(new ChessMove(myPosition, new ChessPosition(pawnRow, pawnCol), ChessPiece.PieceType.BISHOP));
                        moves.add(new ChessMove(myPosition, new ChessPosition(pawnRow, pawnCol), ChessPiece.PieceType.QUEEN));
                        moves.add(new ChessMove(myPosition, new ChessPosition(pawnRow, pawnCol), ChessPiece.PieceType.KNIGHT));
                    }
                }
            }
            return moves;
        }
        return new HashSet<>();
        //throw new RuntimeException("Not implemented");
    }
}
