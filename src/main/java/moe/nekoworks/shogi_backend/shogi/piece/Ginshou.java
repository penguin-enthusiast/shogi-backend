package moe.nekoworks.shogi_backend.shogi.piece;

import moe.nekoworks.shogi_backend.shogi.Square;

public class Ginshou extends PromotablePiece {

    public Ginshou(boolean isSente) {
        super(isSente);
    }

    public Ginshou(Square square, boolean isSente, boolean isPromoted) {
        super(square, isSente, isPromoted);
    }

    @Override
    public PieceEnum getPieceEnum() {
        return isPromoted ? PieceEnum.PGIN : PieceEnum.GIN;
    }

}
