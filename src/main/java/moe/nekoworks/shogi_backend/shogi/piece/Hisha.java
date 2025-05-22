package moe.nekoworks.shogi_backend.shogi.piece;

import moe.nekoworks.shogi_backend.shogi.Square;

public class Hisha extends PromotablePiece {

    public Hisha(boolean isSente) {
        super(isSente);
    }

    public Hisha(Square square, boolean isSente, boolean isPromoted) {
        super(square, isSente, isPromoted);
    }

    @Override
    public PieceEnum getPieceEnum() {
        return isPromoted ? PieceEnum.RYUU : PieceEnum.HI;
    }

}
