package moe.nekoworks.shogi_backend.model;

import moe.nekoworks.shogi_backend.shogi.Board;
import moe.nekoworks.shogi_backend.shogi.Square;
import moe.nekoworks.shogi_backend.shogi.move.BoardMove;

public class MoveRequest {

    private final int originX;
    private final int originY;
    private final int targetX;
    private final int targetY;
    private final boolean isPromotion;

    public MoveRequest(int originX, int originY, int targetX, int targetY, boolean isPromotion) {
        this.originX = originX;
        this.originY = originY;
        this.targetX = targetX;
        this.targetY = targetY;
        this.isPromotion = isPromotion;
    }

    public BoardMove buildMove(Board board) {
        Square originSquare = board.getSquare(originX, originY);
        Square targetSquare = board.getSquare(targetX, targetY);
        if (originSquare.getPiece() == null) {
            return null;
        }
        return new BoardMove(originSquare.getPiece(), targetSquare, isPromotion);
    }
}
