package moe.nekoworks.shogi_backend.shogi.piece;

import moe.nekoworks.shogi_backend.shogi.Square;

public class Kakugyou extends PromotablePiece {

    public Kakugyou(boolean isSente) {
        super(isSente);
    }

    public Kakugyou(Square square, boolean isSente, boolean isPromoted) {
        super(square, isSente, isPromoted);
    }

    @Override
    public PieceEnum getPieceEnum() {
        return isPromoted ? PieceEnum.UMA : PieceEnum.KAKU;
    }

}
