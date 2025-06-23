package moe.nekoworks.shogi_backend.model;

import moe.nekoworks.shogi_backend.shogi.Board;
import moe.nekoworks.shogi_backend.shogi.move.AbstractMove;

public abstract class AbstractSGBoardAction<T extends AbstractMove> {

    private long timestamp;

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public abstract T buildMove(Board board);

    public abstract String type();
}
