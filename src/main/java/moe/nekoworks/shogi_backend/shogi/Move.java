package moe.nekoworks.shogi_backend.shogi;

import moe.nekoworks.shogi_backend.shogi.piece.Piece;

import java.util.HashSet;
import java.util.Set;

public class Move {

    private Piece piece;
    private Square targetSquare;
    private boolean isPromotion = false;

    public Move(Piece piece, Square targetSquare, boolean isPromotion) {
        this.piece = piece;
        this.targetSquare = targetSquare;
        this.isPromotion = isPromotion;
    }

    public Move(Piece piece, Square targetSquare) {
        this(piece, targetSquare, false);
    }

    public Piece getPiece() {
        return piece;
    }

    public void setPiece(Piece piece) {
        this.piece = piece;
    }

    public Square getTargetSquare() {
        return targetSquare;
    }

    public void setTargetSquare(Square targetSquare) {
        this.targetSquare = targetSquare;
    }

    public boolean isPromotion() {
        return isPromotion;
    }

    public void setPromotion(boolean promotion) {
        isPromotion = promotion;
    }

    public Square getOriginSquare() {
        return piece.getSquare();
    }

    public String notationJP () {
        StringBuilder sb = new StringBuilder(6);
        sb.append(piece.isSente() ? '☗' : '☖');
        sb.append(targetSquare.getSquareNameJp());
        sb.append(piece.getPieceEnum().getNameJPShort());
        if (isPromotion) {
            sb.append('成');
        }

        return sb.toString();
    }

    @Override
    public String toString () {
        return notationJP();
    }

}
