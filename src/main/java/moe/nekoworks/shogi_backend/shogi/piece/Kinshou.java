package moe.nekoworks.shogi_backend.shogi.piece;

import moe.nekoworks.shogi_backend.shogi.Board;
import moe.nekoworks.shogi_backend.shogi.Move;
import moe.nekoworks.shogi_backend.shogi.Square;

import java.util.HashSet;
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
    public Set<Move> updateLegalMoves(Board board) {
        legalMoves = getGoldMoves(board);
        return legalMoves;
    }

}
