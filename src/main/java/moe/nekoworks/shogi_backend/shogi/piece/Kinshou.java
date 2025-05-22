package moe.nekoworks.shogi_backend.shogi.piece;

import moe.nekoworks.shogi_backend.shogi.Board;
import moe.nekoworks.shogi_backend.shogi.Move;
import moe.nekoworks.shogi_backend.shogi.Square;

import java.util.Set;

public class Kinshou extends Piece {

    public Kinshou(Square square, boolean isSente) {
        super(square, isSente);
    }

    @Override
    public PieceEnum getPieceEnum() {
        return PieceEnum.KIN;
    }

    @Override
    public Set<Move> legalMoves(Board board) {
        return Set.of();
    }

}
