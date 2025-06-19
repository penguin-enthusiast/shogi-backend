package moe.nekoworks.shogi_backend.exception;

public class GameException extends RuntimeException {

    public GameException(String message) {
        super(message);
    }
}
