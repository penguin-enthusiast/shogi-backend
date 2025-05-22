package moe.nekoworks.shogi_backend.shogi.piece;

import moe.nekoworks.shogi_backend.shogi.Board;
import moe.nekoworks.shogi_backend.shogi.move.BoardMove;
import moe.nekoworks.shogi_backend.shogi.Square;
import moe.nekoworks.shogi_backend.shogi.move.MoveHelper;

import java.util.HashSet;
import java.util.Set;

public class Fuhyou extends PromotablePiece {


    public Fuhyou(boolean isSente) {
        super(isSente);
    }

    public Fuhyou(Square square, boolean isSente) {
        super(square, isSente);
    }

    public Fuhyou(Square square, boolean isSente, boolean isPromoted) {
        super(square, isSente, isPromoted);
    }

    @Override
    public PieceEnum getPieceEnum() {
        return isPromoted ? PieceEnum.TO : PieceEnum.FU;
    }

    @Override
    public void promote() {
        isPromoted = true;
    }

}
