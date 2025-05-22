package moe.nekoworks.shogi_backend.shogi.move;

import moe.nekoworks.shogi_backend.shogi.Board;
import moe.nekoworks.shogi_backend.shogi.Square;
import moe.nekoworks.shogi_backend.shogi.piece.PieceEnum;

import java.util.Objects;

public abstract class Move {

    protected final Square targetSquare;

    public Move(Square targetSquare) {
        this.targetSquare = targetSquare;
    }

    public Square getTargetSquare() {
        return targetSquare;
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
        return targetSquare.getSquareNameJP();
    }

    public abstract boolean isSente();

    public abstract PieceEnum getPieceType();

    protected abstract String getDisambiguationJP();

    @Override
    public abstract boolean equals(Object o);

    @Override
    public String toString () {
        return notationJP();
    }
}
