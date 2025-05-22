package moe.nekoworks.shogi_backend.shogi.piece;

import moe.nekoworks.shogi_backend.shogi.Board;
import moe.nekoworks.shogi_backend.shogi.Move;
import moe.nekoworks.shogi_backend.shogi.Square;

import java.util.Set;

public class Keima extends PromotablePiece {

    public Keima(Square square, boolean isSente) {
        super(square, isSente);
    }

    public Keima(Square square, boolean isSente, boolean isPromoted) {
        super(square, isSente, isPromoted);
    }

    @Override
    public PieceEnum getPieceEnum() {
        return isPromoted ? PieceEnum.PKEI : PieceEnum.KEI;
    }

    @Override
    public Set<Move> legalMoves(Board board) {
        return Set.of();
    }

    @Override
    public void promote() {
        isPromoted = true;
    }

}
