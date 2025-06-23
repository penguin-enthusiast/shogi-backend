package moe.nekoworks.shogi_backend.shogi.engine;

import moe.nekoworks.shogi_backend.shogi.Board;
import moe.nekoworks.shogi_backend.shogi.move.AbstractMove;

public abstract class Engine {

    // only plays as gote for now
    private boolean isSente = false;
    private final Board board;

    public Engine(Board board) {
        this.board = board;
    }

    public boolean isSente() {
        return isSente;
    }

    public void setSente(boolean sente) {
        isSente = sente;
    }

    public static Engine getEngineFromName(String engineName, Board board) {
        switch (engineName) {
            case "random":
                return new RandomMoveEngine(board);
            default:
                return null;
        }
    }

    public abstract String getName();

    public abstract AbstractMove makeEngineMove(Board board);
}
