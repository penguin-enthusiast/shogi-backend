package moe.nekoworks.shogi_backend.shogi.piece;

import moe.nekoworks.shogi_backend.shogi.Square;

public class Keima extends PromotablePiece {

    public Keima(boolean isSente) {
        super(isSente);
    }

    public Keima(Square square, boolean isSente, boolean isPromoted) {
        super(square, isSente, isPromoted);
    }

    @Override
    public PieceEnum getPieceEnum() {
        return isPromoted ? PieceEnum.PKEI : PieceEnum.KEI;
    }

}
