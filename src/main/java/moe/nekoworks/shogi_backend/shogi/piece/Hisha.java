package moe.nekoworks.shogi_backend.shogi.piece;

import moe.nekoworks.shogi_backend.shogi.Board;
import moe.nekoworks.shogi_backend.shogi.Move;
import moe.nekoworks.shogi_backend.shogi.Square;

import java.util.HashSet;
import java.util.Set;

public class Hisha extends PromotablePiece {

    public Hisha(Square square, boolean isSente) {
        super(square, isSente);
    }

    public Hisha(Square square, boolean isSente, boolean isPromoted) {
        super(square, isSente, isPromoted);
    }

    @Override
    public PieceEnum getPieceEnum() {
        return isPromoted ? PieceEnum.RYUU : PieceEnum.HI;
    }

    @Override
    public Set<Move> updateLegalMoves(Board board) {
        // moves when not promoted
        //  .  ↑  .    .  ↑  .
        //  ←  ☗  →    ←  ⛊  →
        //  .  ↓  .    .  ↓  .
        //
        // moves when promoted
        //  O  ↑  O    O  ↑  O
        //  ←  ☗  →    ←  ⛊  →
        //  O  ↓  O    O  ↓  O
        if (isPromoted) {
            return getGoldMoves(board);
        }
        HashSet<Move> moves = new HashSet<Move>();
        boolean isSente = isSente();
        int x = getSquare().getX();
        int y = getSquare().getY();

        boolean moveAdded;
        moveAdded = false;
        int destY = y;
        do {
            destY++;
            moveAdded = createMove(board, x, destY, moves, isSente, !isPromoted);
        } while (moveAdded);
        destY = y;
        do {
            destY--;
            moveAdded = createMove(board, x, destY, moves, isSente, !isPromoted);
        } while (moveAdded);
        int destX = x;
        do {
            destX++;
            moveAdded = createMove(board, destX, y, moves, isSente, !isPromoted);
        } while (moveAdded);
        destX = x;
        do {
            destX--;
            moveAdded = createMove(board, destX, y, moves, isSente, !isPromoted);
        } while (moveAdded);

        if (isPromoted) {
            createMove(board, x + 1, y - 1, moves, isSente, false);
            createMove(board, x + 1, y + 1, moves, isSente, false);
            createMove(board, x - 1, y - 1, moves, isSente, false);
            createMove(board, x - 1, y + 1, moves, isSente, false);
        }

        return moves;
    }

    @Override
    public void promote() {
        isPromoted = true;
    }

}
