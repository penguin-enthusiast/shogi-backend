package moe.nekoworks.shogi_backend.shogi.piece;

import moe.nekoworks.shogi_backend.shogi.Board;
import moe.nekoworks.shogi_backend.shogi.Move;
import moe.nekoworks.shogi_backend.shogi.Square;

import java.util.HashSet;
import java.util.Set;

public class Kakugyou extends PromotablePiece {

    public Kakugyou(boolean isSente) {
        super(isSente);
    }

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
    public Set<Move> updateLegalMoves(Board board) {
        // moves when not promoted
        //  ↖  .  ↗    ↖  .  ↗
        //  .  ☗  .    .  ⛊  .
        //  ↙  .  ↘    ↙  .  ↘
        //
        // moves when promoted
        //  ↖  O  ↗    ↖  O  ↗
        //  O  ☗  O    O  ⛊  O
        //  ↙  O  ↘    ↙  O  ↘
        HashSet<Move> moves = new HashSet<Move>();
        boolean isSente = isSente();
        int x = getSquare().getX();
        int y = getSquare().getY();

        boolean moveAdded;
        moveAdded = false;
        int destX = x;
        int destY = y;
        do {
            destX++;
            destY++;
            moveAdded = createMove(board, destX, destY, moves, isSente, !isPromoted);
        } while (moveAdded);
        destY = y;
        destX = x;
        do {
            destX++;
            destY--;
            moveAdded = createMove(board, destX, destY, moves, isSente, !isPromoted);
        } while (moveAdded);
        destY = y;
        destX = x;
        do {
            destX--;
            destY++;
            moveAdded = createMove(board, destX, destY, moves, isSente, !isPromoted);
        } while (moveAdded);
        destY = y;
        destX = x;
        do {
            destX--;
            destY--;
            moveAdded = createMove(board, destX, destY, moves, isSente, !isPromoted);
        } while (moveAdded);

        if (isPromoted) {
            createMove(board, x + 1, y, moves, isSente, false);
            createMove(board, x - 1, y, moves, isSente, false);
            createMove(board, x, y + 1, moves, isSente, false);
            createMove(board, x, y - 1, moves, isSente, false);
        }

        legalMoves = moves;
        return legalMoves;
    }

    @Override
    public void promote() {
        isPromoted = true;
    }

}
