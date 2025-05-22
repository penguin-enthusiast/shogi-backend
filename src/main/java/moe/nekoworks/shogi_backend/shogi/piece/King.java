package moe.nekoworks.shogi_backend.shogi.piece;

import moe.nekoworks.shogi_backend.shogi.Board;
import moe.nekoworks.shogi_backend.shogi.Move;
import moe.nekoworks.shogi_backend.shogi.Square;

import java.util.HashSet;
import java.util.Set;

public abstract class King extends Piece {

    public King(Square square, boolean isSente) {
        super(square, isSente);
    }
    @Override
    public Set<Move> updateLegalMoves(Board board) {
        // moves like
        //  O  O  O    O  O  O
        //  O  ☗  O    O  ⛊  O
        //  O  O  O    O  O  O
        HashSet<Move> moves = new HashSet<Move>();
        int x = getSquare().getX();
        int y = getSquare().getY();
        createMove(board, x + 1, y, moves, isSente(), false);
        createMove(board, x - 1, y, moves, isSente(), false);
        createMove(board, x, y + 1, moves, isSente(), false);
        createMove(board, x, y - 1, moves, isSente(), false);
        createMove(board, x + 1, y + 1, moves, isSente(), false);
        createMove(board, x - 1, y + 1, moves, isSente(), false);
        createMove(board, x + 1, y - 1, moves, isSente(), false);
        createMove(board, x - 1, y - 1, moves, isSente(), false);

        return moves;
    }

    @Override
    public final void putInHand() {
        return;
    }

}
