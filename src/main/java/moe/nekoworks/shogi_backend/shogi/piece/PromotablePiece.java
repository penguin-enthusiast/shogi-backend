package moe.nekoworks.shogi_backend.shogi.piece;

import moe.nekoworks.shogi_backend.shogi.Square;

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

    public boolean isPromoted() {
        return isPromoted;
    }

    public abstract void promote();

}
