package moe.nekoworks.shogi_backend.model;

import moe.nekoworks.shogi_backend.shogi.engine.Engine;
import moe.nekoworks.shogi_backend.shogi.move.AbstractMove;

public class EngineGame extends Game {

    private final Engine engine;

    // for now players only play as sente against engine
    public EngineGame(String player1, Engine engine) {
        super(player1);
        this.engine = engine;
        setPlayer2(engine.getName());
    }

    public AbstractMove makeEngineMove() {
        AbstractMove move = engine.makeEngineMove(getBoard());
        getBoard().commitMove(move);
        return move;
    }
}
