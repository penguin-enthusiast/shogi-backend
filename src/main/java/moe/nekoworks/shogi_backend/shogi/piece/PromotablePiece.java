package moe.nekoworks.shogi_backend.shogi.piece;

import moe.nekoworks.shogi_backend.shogi.Board;
import moe.nekoworks.shogi_backend.shogi.Square;
import moe.nekoworks.shogi_backend.shogi.move.BoardMove;
import moe.nekoworks.shogi_backend.shogi.move.MoveHelper;

import java.util.Set;

public abstract class PromotablePiece extends Piece{

    protected boolean isPromoted = false;

    public PromotablePiece(Square square, boolean isSente) {
        super(square, isSente);
    }

    public PromotablePiece(Square square, boolean isSente, boolean isPromoted) {
        super(square, isSente);
        this.isPromoted = isPromoted;
    }

    public PromotablePiece(boolean isSente) {
        super(isSente);
    }

    @Override
    public void putInHand() {
        super.putInHand();
        isPromoted = false;
    }

    @Override
    public void move(BoardMove move) {
        super.move(move);
        if (move.isPromotion()) {
            promote();
        }
    }

    @Override
    protected boolean createMove(Board board, Piece piece, Square targetSquare, Set<BoardMove> moves) {
        boolean allowPromotion = !isPromoted &&
                (targetSquare.isPromotionZone(isSente()) || piece.getSquare().isPromotionZone(isSente()));
        return MoveHelper.createMove(this, targetSquare, moves, allowPromotion);
    }

    @Override
    public boolean isPromoted() {
        return isPromoted;
    }

    public void promote() {
        isPromoted = true;
    }

}
