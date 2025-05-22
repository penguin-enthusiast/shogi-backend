package moe.nekoworks.shogi_backend.shogi.piece;

import moe.nekoworks.shogi_backend.shogi.Square;

public class Kinshou extends Piece {

    public Kinshou(boolean isSente) {
        super(isSente);
    }

    public Kinshou(Square square, boolean isSente) {
        super(square, isSente);
    }

    @Override
    public PieceEnum getPieceEnum() {
        return PieceEnum.KIN;
    }

}
