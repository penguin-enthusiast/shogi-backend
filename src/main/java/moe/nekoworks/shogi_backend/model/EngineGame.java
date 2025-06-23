package moe.nekoworks.shogi_backend.model;

import moe.nekoworks.shogi_backend.shogi.engine.Engine;
import moe.nekoworks.shogi_backend.shogi.move.AbstractMove;

public class EngineGame extends Game {

    private final Engine engine;

    // for now players only play as sente against engine
    public EngineGame(String player1, String engineName) {
        super(player1);
        Engine engine = Engine.getEngineFromName(engineName, getBoard());
        assert engine != null;
        this.engine = engine;
        setPlayer2(engine.getName());
    }

    public AbstractMove makeEngineMove() {
        AbstractMove move = engine.makeEngineMove(getBoard());
        if (move == null) {
            return null;
        }
        getBoard().commitMove(move);
        return move;
    }
}
