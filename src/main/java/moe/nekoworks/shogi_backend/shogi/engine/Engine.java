package moe.nekoworks.shogi_backend.shogi.engine;

import moe.nekoworks.shogi_backend.shogi.Board;
import moe.nekoworks.shogi_backend.shogi.move.AbstractMove;

public abstract class Engine {

    // only plays as gote for now
    private boolean isSente = false;

    public boolean isSente() {
        return isSente;
    }

    public void setSente(boolean sente) {
        isSente = sente;
    }

    public static Engine getEngineFromName(String engineName) {
        switch (engineName) {
            case "random":
                return new RandomMoveEngine();
            default:
                return null;
        }
    }

    public abstract String getName();

    public abstract AbstractMove makeEngineMove(Board board);
}
