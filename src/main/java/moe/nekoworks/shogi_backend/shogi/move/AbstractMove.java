package moe.nekoworks.shogi_backend.shogi.move;

import moe.nekoworks.shogi_backend.misc.TimeUtils;
import moe.nekoworks.shogi_backend.shogi.Board;
import moe.nekoworks.shogi_backend.shogi.Square;
import moe.nekoworks.shogi_backend.shogi.piece.Piece;
import moe.nekoworks.shogi_backend.shogi.piece.PieceEnum;

import java.util.Objects;
import java.util.Set;

public abstract class AbstractMove {

    protected final Square targetSquare;
    private final boolean isSente;

    private long timestamp;

    public AbstractMove(Square targetSquare, boolean isSente) {
        this.targetSquare = targetSquare;
        this.isSente = isSente;
    }

    public Square getTargetSquare() {
        return targetSquare;
    }

    public boolean isSente() {
        return isSente;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    protected Board getBoard() {
        return targetSquare.getBoard();
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(targetSquare);
    }

    public String notationJP() {
        return (isSente() ? '☗' : '☖') +
                getTargetSquareNameJP() +
                getPieceType().getNameJPShort() +
                getDisambiguationJP();
    }

    public String getMoveTime() {
        return TimeUtils.convertTimeHumanReadable(getBoard().getStartTimeStamp(), getTimestamp());
    }

    public abstract String notationInt();

    protected String getTargetSquareNameJP() {
        if (getBoard().getLastMove() != null && targetSquare == getBoard().getLastMove().targetSquare) {
            return "同　";
        }
        return targetSquare.toString();
    }

    public abstract PieceEnum getPieceType();

    public abstract boolean isKingCapture();

    public abstract void makeMove();

    public abstract void undoMove();

    public abstract boolean offCooldown();

    protected boolean isAmbiguous() {
        return !getAmbiguousPieces().isEmpty();
    }

    protected abstract Set<Piece> getAmbiguousPieces();

    protected abstract String getDisambiguationJP();

    @Override
    public abstract boolean equals(Object o);

    @Override
    public String toString () {
        return notationInt() + "-" + getMoveTime();
    }
}
