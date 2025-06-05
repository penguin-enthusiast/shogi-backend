package moe.nekoworks.shogi_backend.shogi.engine;

import moe.nekoworks.shogi_backend.shogi.Board;
import moe.nekoworks.shogi_backend.shogi.move.AbstractMove;
import moe.nekoworks.shogi_backend.shogi.move.BoardMove;
import moe.nekoworks.shogi_backend.shogi.move.DropMove;

import java.util.ArrayList;
import java.util.Random;
import java.util.Set;

public class RandomMoveEngine extends Engine {

    @Override
    public AbstractMove makeEngineMove(Board board) {
        Set<BoardMove> boardMoves = board.getBoardMoves(isSente());
        Set<DropMove> dropMoves = board.getDropMoves(isSente());
        int size = boardMoves.size() + dropMoves.size();
        ArrayList<AbstractMove> legalMoves = new ArrayList<>(size);
        legalMoves.addAll(boardMoves);
        legalMoves.addAll(dropMoves);
        Random random = new Random();
        return legalMoves.get(random.nextInt(0, size));
    }

    @Override
    public String getName() {
        return "random";
    }
}
