package moe.nekoworks.shogi_backend.shogi.piece;

import moe.nekoworks.shogi_backend.shogi.Square;

public class Kyousha extends PromotablePiece {

    public Kyousha(boolean isSente) {
        super(isSente);
    }

    public Kyousha(Square square, boolean isSente, boolean isPromoted) {
        super(square, isSente, isPromoted);
    }

    @Override
    public PieceEnum getPieceEnum() {
        return isPromoted ? PieceEnum.PKYOU : PieceEnum.KYOU;
    }

}
