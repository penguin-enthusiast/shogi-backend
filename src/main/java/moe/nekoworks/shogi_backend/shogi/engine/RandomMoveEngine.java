package moe.nekoworks.shogi_backend.shogi.engine;

import moe.nekoworks.shogi_backend.model.EngineGame;
import moe.nekoworks.shogi_backend.model.Game;
import moe.nekoworks.shogi_backend.model.GameStatus;
import moe.nekoworks.shogi_backend.shogi.Board;
import moe.nekoworks.shogi_backend.shogi.move.AbstractMove;
import moe.nekoworks.shogi_backend.shogi.move.BoardMove;
import moe.nekoworks.shogi_backend.shogi.move.DropMove;

import java.util.ArrayList;
import java.util.Random;
import java.util.Set;

public class RandomMoveEngine extends Engine {

    public RandomMoveEngine(Game game) {
        super(game);
    }

    public AbstractMove getEngineMove() {
        Board board = game.getBoard();
        Set<BoardMove> boardMoves = board.getBoardMoves(isSente());
        Set<DropMove> dropMoves = board.getDropMoves(isSente());
        int size = boardMoves.size() + dropMoves.size();
        ArrayList<AbstractMove> legalMoves = new ArrayList<>(size);
        int total = 0;
        for (BoardMove move : boardMoves) {
            if (move.offCooldown()) {
                legalMoves.add(move);
                total++;
            }
        }
        for (DropMove move : dropMoves) {
            if (move.offCooldown()) {
                legalMoves.add(move);
                total++;
            }
        }
        Random random = new Random();
        if (total == 0) {
            return null;
        }
        return legalMoves.get(random.nextInt(0, total));
    }

    @Override
    public String getName() {
        return "random";
    }

    @Override
    public void run() {
        while(game.getStatus() == GameStatus.IN_PROGRESS) {
            try {
                // makes random move at a rate of 2 per length of cooldown interval
                Thread.sleep(game.getCooldownTime()/2);
                ((EngineGame) this.game).makeEngineMove(getEngineMove());
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
