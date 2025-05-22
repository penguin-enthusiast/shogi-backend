package moe.nekoworks.shogi_backend.shogi.piece;

import moe.nekoworks.shogi_backend.shogi.Board;
import moe.nekoworks.shogi_backend.shogi.Move;
import moe.nekoworks.shogi_backend.shogi.Square;

import java.util.Set;

public abstract class Piece {

    private Square square;
    private boolean isSente;
    private boolean inHand = false;
    private double lastMoved = 0;

    public Piece (Square square, boolean isSente) {
        this.square = square;
        this.isSente = isSente;
        square.setPiece(this);
    }

    public double getLastMoved() {
        return lastMoved;
    }

    public void setLastMoved(double lastMoved) {
        this.lastMoved = lastMoved;
    }

    public boolean isSente() {
        return isSente;
    }

    public void setSente(boolean sente) {
        isSente = sente;
    }

    public boolean isInHand() {
        return inHand;
    }

    public void setInHand(boolean inHand) {
        this.inHand = inHand;
    }

    public Square getSquare() {
        return square;
    }

    // should only ever be called by the move() method
    public void setSquare(Square square) {
        this.square = square;
    }

    public abstract PieceEnum getPieceEnum();

    public abstract Set<Move> legalMoves(Board board);

    public String getName() {
        return getPieceEnum().getNameJPShort();
    }
}
