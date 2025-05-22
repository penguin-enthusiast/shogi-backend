package moe.nekoworks.shogi_backend.shogi.piece;

import moe.nekoworks.shogi_backend.shogi.Board;
import moe.nekoworks.shogi_backend.shogi.Move;
import moe.nekoworks.shogi_backend.shogi.Square;

import java.util.HashSet;
import java.util.Set;

public class Ginshou extends PromotablePiece {

    public Ginshou(Square square, boolean isSente) {
        super(square, isSente);
    }

    public Ginshou(Square square, boolean isSente, boolean isPromoted) {
        super(square, isSente, isPromoted);
    }

    @Override
    public PieceEnum getPieceEnum() {
        return isPromoted ? PieceEnum.PGIN : PieceEnum.GIN;
    }

    @Override
    public Set<Move> updateLegalMoves(Board board) {
        // moves when not promoted
        //  O  O  O    O  .  O
        //  .  ☗  .    .  ⛊  .
        //  O  .  O    O  O  O
        //
        // moves like a gold when promoted
        if (isPromoted) {
            return getGoldMoves(board);
        }
        HashSet<Move> moves = new HashSet<Move>();
        int x = getSquare().getX();
        int y = getSquare().getY();
        createMove(board, x + 1, y + 1, moves, isSente(), true);
        createMove(board, x + 1, y - 1, moves, isSente(), true);
        createMove(board, x - 1, y + 1, moves, isSente(), true);
        createMove(board, x - 1, y - 1, moves, isSente(), true);

        y = isSente() ? y - 1 : y + 1;
        createMove(board, x, y, moves, isSente(), true);

        return moves;
    }

    @Override
    public void promote() {
        isPromoted = true;
    }

}
