package moe.nekoworks.shogi_backend.shogi.move;

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

    public abstract PieceEnum getPieceType();

    @Override
    public abstract boolean equals(Object o);

    @Override
    public int hashCode() {
        return Objects.hashCode(targetSquare);
    }

    public abstract String notationJP ();

    @Override
    public String toString () {
        return notationJP();
    }
}
