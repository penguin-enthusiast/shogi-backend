package moe.nekoworks.shogi_backend.shogi.piece;

import moe.nekoworks.shogi_backend.shogi.Board;
import moe.nekoworks.shogi_backend.shogi.Move;
import moe.nekoworks.shogi_backend.shogi.Square;

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
    public Set<Move> updateLegalMoves(Board board) {
        // moves when not promoted
        //  .  O  .    .  .  .
        //  .  ☗  .    .  ⛊  .
        //  .  .  .    .  O  .
        //
        // moves like a gold when promoted
        if (isPromoted) {
            legalMoves = getGoldMoves(board);
            return legalMoves;
        }
        HashSet<Move> moves = new HashSet<Move>();
        int x = getSquare().getX();
        int y = getSquare().getY() + (isSente() ? -1 : 1);
        createMove(board, x, y, moves, isSente(), true);

        legalMoves = moves;
        return legalMoves;
    }

    @Override
    public void promote() {
        isPromoted = true;
    }

}
