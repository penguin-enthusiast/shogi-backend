package moe.nekoworks.shogi_backend.shogi.piece;

import moe.nekoworks.shogi_backend.shogi.Board;
import moe.nekoworks.shogi_backend.shogi.Move;
import moe.nekoworks.shogi_backend.shogi.Square;

import java.util.Set;

public class Kyousha extends PromotablePiece {

    public static final String NAME_INT_SHORT = "Kyou";
    public static final String NAME_INT_LONG = "Kyousha";
    public static final String NAME_JP_SHORT = "香";
    public static final String NAME_JP_LONG = "香車";

    public Kyousha(Square square, boolean isSente) {
        super(square, isSente);
    }

    public Kyousha(Square square, boolean isSente, boolean isPromoted) {
        super(square, isSente, isPromoted);
    }

    @Override
    public PieceEnum getPieceEnum() {
        return isPromoted ? PieceEnum.PKYOU : PieceEnum.KYOU;
    }

    @Override
    public Set<Move> legalMoves(Board board) {
        return Set.of();
    }

    @Override
    public void promote() {
        isPromoted = true;
    }

    @Override
    public String getName() {
        return NAME_JP_SHORT;
    }
}
