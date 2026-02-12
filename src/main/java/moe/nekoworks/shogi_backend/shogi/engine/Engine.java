package moe.nekoworks.shogi_backend.shogi.engine;

import moe.nekoworks.shogi_backend.model.Game;
import moe.nekoworks.shogi_backend.shogi.Board;
import moe.nekoworks.shogi_backend.shogi.move.AbstractMove;

public abstract class Engine implements Runnable {

    // only plays as gote for now
    private boolean isSente = false;
    protected final Game game;

    public Engine(Game game) {
        this.game = game;
    }

    public boolean isSente() {
        return isSente;
    }

    public void setSente(boolean sente) {
        isSente = sente;
    }

    public static Engine getEngineFromName(String engineName, Game game) {
        switch (engineName) {
            case "random":
                return new RandomMoveEngine(game);
            default:
                return null;
        }
    }

    public abstract String getName();

    // engine logic should go here
    public abstract void run();
}
