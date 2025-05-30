package moe.nekoworks.shogi_backend.shogi.move;

import moe.nekoworks.shogi_backend.shogi.Board;
import moe.nekoworks.shogi_backend.shogi.Square;
import moe.nekoworks.shogi_backend.shogi.piece.PieceEnum;

import java.util.Objects;

public abstract class AbstractMove {

    protected final Square targetSquare;
    private final boolean isSente;

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

    protected Board getBoard() {
        return targetSquare.getBoard();
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(targetSquare);
    }

    public String notationJP(){
        return (isSente() ? '☗' : '☖') +
                getTargetSquareName(targetSquare) +
                getPieceType().getNameJPShort() +
                getDisambiguationJP();
    }

    private String getTargetSquareName(Square square) {
        if (getBoard().getLastMove() != null) {
            Square prevSquare = getBoard().getLastMove().targetSquare;
            if (square == prevSquare) {
                return "同　";
            }
        }
        return targetSquare.getSquareNameJP();
    }

    public abstract PieceEnum getPieceType();

    public abstract boolean makeMove();

    public abstract void undoMove();

    protected abstract String getDisambiguationJP();

    @Override
    public abstract boolean equals(Object o);

    @Override
    public String toString () {
        return notationJP();
    }
}
