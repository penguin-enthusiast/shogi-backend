package moe.nekoworks.shogi_backend.shogi.piece;

import moe.nekoworks.shogi_backend.shogi.Board;
import moe.nekoworks.shogi_backend.shogi.move.BoardMove;
import moe.nekoworks.shogi_backend.shogi.Square;
import moe.nekoworks.shogi_backend.shogi.move.MoveHelper;

import java.util.HashSet;
import java.util.Set;

public class Kyousha extends PromotablePiece {

    public Kyousha(boolean isSente) {
        super(isSente);
    }

    public Kyousha(Square square, boolean isSente) {
        super(square, isSente);
    }

    public Kyousha(Square square, boolean isSente, boolean isPromoted) {
        super(square, isSente, isPromoted);
    }

    @Override
    public PieceEnum getPieceEnum() {
        return isPromoted ? PieceEnum.PKYOU : PieceEnum.KYOU;
    }

    @Override
    public void promote() {
        isPromoted = true;
    }
}
