package moe.nekoworks.shogi_backend.shogi.piece;

import moe.nekoworks.shogi_backend.shogi.Square;

public class Fuhyou extends PromotablePiece {

    public Fuhyou(boolean isSente) {
        super(isSente);
    }

    public Fuhyou(Square square, boolean isSente, boolean isPromoted) {
        super(square, isSente, isPromoted);
    }

    @Override
    public PieceEnum getPieceEnum() {
        return isPromoted ? PieceEnum.TO : PieceEnum.FU;
    }

}
