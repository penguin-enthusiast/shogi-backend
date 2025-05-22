package moe.nekoworks.shogi_backend.shogi.piece;

import moe.nekoworks.shogi_backend.shogi.Board;
import moe.nekoworks.shogi_backend.shogi.Move;
import moe.nekoworks.shogi_backend.shogi.Square;

import java.util.Set;

public class Kakugyou extends PromotablePiece {

    public Kakugyou(Square square, boolean isSente) {
        super(square, isSente);
    }

    public Kakugyou(Square square, boolean isSente, boolean isPromoted) {
        super(square, isSente, isPromoted);
    }

    @Override
    public PieceEnum getPieceEnum() {
        return isPromoted ? PieceEnum.UMA : PieceEnum.KAKU;
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
