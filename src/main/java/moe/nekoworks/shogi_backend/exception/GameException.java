package moe.nekoworks.shogi_backend.exception;

public class GameException extends RuntimeException {

    public GameException(String msg) {
        super(msg);
    }
}
