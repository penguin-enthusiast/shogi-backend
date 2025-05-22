package moe.nekoworks.shogi_backend.shogi.move;

import moe.nekoworks.shogi_backend.shogi.Square;
import moe.nekoworks.shogi_backend.shogi.piece.PieceEnum;

import java.util.Objects;

public class DropMove extends Move {

    private final boolean isSente;
    private final PieceEnum piece;

    public DropMove(Square targetSquare, PieceEnum piece, boolean isSente) {
        super(targetSquare);
        this.isSente = isSente;
        this.piece = piece;
    }

    @Override
    public PieceEnum getPieceType() {
        return piece;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        DropMove move = (DropMove) o;
        return piece == move.piece && getTargetSquare() == move.getTargetSquare();
    }


    @Override
    public String notationJP () {
        StringBuilder sb = new StringBuilder(6);
        sb.append(isSente ? '☗' : '☖');
        sb.append(targetSquare.getSquareNameJp());
        sb.append(piece.getNameJPShort());

        return sb.toString();
    }
}
