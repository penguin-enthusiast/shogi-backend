package moe.nekoworks.shogi_backend.shogi.move;

import moe.nekoworks.shogi_backend.shogi.Square;
import moe.nekoworks.shogi_backend.shogi.piece.Piece;
import moe.nekoworks.shogi_backend.shogi.piece.PieceEnum;

import java.util.Objects;

public class BoardMove extends Move {

    private final Piece piece;
    private final boolean isPromotion;

    public BoardMove(Piece piece, Square targetSquare, boolean isPromotion) {
        super(targetSquare);
        this.piece = piece;
        this.isPromotion = isPromotion;
    }

    public BoardMove(Piece piece, Square targetSquare) {
        this(piece, targetSquare, false);
    }

    public Piece getPiece() {
        return piece;
    }

    public boolean isPromotion() {
        return isPromotion;
    }

    @Override
    public PieceEnum getPieceType () {
        return piece.getPieceEnum();
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        BoardMove boardMove = (BoardMove) o;
        return isPromotion == boardMove.isPromotion && piece == boardMove.piece && targetSquare == boardMove.targetSquare;
    }

    @Override
    public int hashCode() {
        return Objects.hash(piece, targetSquare, isPromotion);
    }

    @Override
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

}
