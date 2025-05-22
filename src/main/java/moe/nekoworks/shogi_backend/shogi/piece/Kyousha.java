package moe.nekoworks.shogi_backend.shogi.piece;

import moe.nekoworks.shogi_backend.shogi.Board;
import moe.nekoworks.shogi_backend.shogi.Move;
import moe.nekoworks.shogi_backend.shogi.Square;

import java.util.HashSet;
import java.util.Set;

public class Kyousha extends PromotablePiece {

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
    public Set<Move> updateLegalMoves(Board board) {
        // moves when not promoted
        //  .  ↑  .    .  .  .
        //  .  ☗  .    .  ⛊  .
        //  .  .  .    .  ↓  .
        //
        // moves like a gold when promoted
        if (isPromoted) {
            return getGoldMoves(board);
        }
        HashSet<Move> moves = new HashSet<Move>();
        boolean isSente = isSente();
        int x = getSquare().getX();
        int y = getSquare().getY();

        boolean moveAdded = false;
        int destY = y;
        do {
            destY = isSente() ? destY - 1 : destY + 1;
            moveAdded = createMove(board, x, y, moves, isSente, true);
        } while (moveAdded);

        return moves;
    }

    @Override
    public void promote() {
        isPromoted = true;
    }
}
