package moe.nekoworks.shogi_backend.model;

import moe.nekoworks.shogi_backend.controller.GameController;
import moe.nekoworks.shogi_backend.service.GameService;
import moe.nekoworks.shogi_backend.shogi.engine.Engine;
import moe.nekoworks.shogi_backend.shogi.move.AbstractMove;
import moe.nekoworks.shogi_backend.shogi.move.BoardMove;
import moe.nekoworks.shogi_backend.shogi.move.DropMove;

public class EngineGame extends Game {

    private final Engine engine;
    private final GameController gameController;

    private Thread engineThread;

    // for now players only play as sente against engine
    public EngineGame(String player1, String engineName, GameController gameController) {
        super(player1);
        this.gameController = gameController;
        Engine engine = Engine.getEngineFromName(engineName, this);
        assert engine != null;
        this.engine = engine;
        setPlayer2(engine.getName());
    }

    @Override
    public void startGame() {
        super.startGame();
        engineThread = new Thread(engine);
        engineThread.start();
    }

    @Override
    public void finishGame() {
        super.finishGame();
        engineThread.interrupt();
    }

    public void makeEngineMove(AbstractMove move) {
        if (move.getClass() == BoardMove.class) {
            gameController.makeAbstractMove(getGameId(), getPlayer2(), new Move((BoardMove) move));
        } else if (move.getClass() == DropMove.class) {
            gameController.makeAbstractMove(getGameId(), getPlayer2(), new Drop((DropMove) move));
        }
    }
}
