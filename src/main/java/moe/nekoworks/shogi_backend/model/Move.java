package moe.nekoworks.shogi_backend.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import moe.nekoworks.shogi_backend.shogi.Board;
import moe.nekoworks.shogi_backend.shogi.Square;
import moe.nekoworks.shogi_backend.shogi.move.BoardMove;

public class Move {

    private final Key orig;
    private final Key dest;
    private final boolean prom;
    private final SGPiece capturedPiece;

    public Move(String orig, String dest, boolean prom, SGPiece capturedPiece) {
        this.orig = new Key(orig);
        this.dest = new Key(dest);
        this.prom = prom;
        this.capturedPiece = capturedPiece;
    }

    public Move(@JsonProperty("orig") String orig, @JsonProperty("dest") String dest, @JsonProperty("prom") boolean prom) {
        this.orig = new Key(orig);
        this.dest = new Key(dest);
        this.prom = prom;
        this.capturedPiece = null;
    }

    public Move(BoardMove boardMove) {
        orig = Key.convertSquareToKey(boardMove.getOriginSquare());
        dest = Key.convertSquareToKey(boardMove.getTargetSquare());
        prom = boardMove.isPromotion();
        capturedPiece = boardMove.isCapture() ? new SGPiece(boardMove.getCapturedPiece()) : null;
    }

    public String getOrig() {
        return orig.toString();
    }

    public String getDest() {
        return dest.toString();
    }

    public boolean isProm() {
        return prom;
    }

    public SGPiece getCapturedPiece() {
        return capturedPiece;
    }

    public BoardMove buildMove(Board board) {
        Square originSquare = board.getSquare(orig.convertToSquare());
        Square targetSquare = board.getSquare(dest.convertToSquare());
        if (originSquare.getPiece() == null) {
            return null;
        }
        return new BoardMove(originSquare.getPiece(), targetSquare, prom);
    }
}
