package moe.nekoworks.shogi_backend.shogi.move;

import moe.nekoworks.shogi_backend.misc.TimeUtils;
import moe.nekoworks.shogi_backend.shogi.Board;
import moe.nekoworks.shogi_backend.shogi.Square;
import moe.nekoworks.shogi_backend.shogi.piece.Piece;
import moe.nekoworks.shogi_backend.shogi.piece.PieceEnum;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

public class DropMove extends AbstractMove {

    private final PieceEnum piece;

    public DropMove(Square targetSquare, PieceEnum piece, boolean isSente) {
        super(targetSquare, isSente);
        this.piece = piece;
    }

    @Override
    public PieceEnum getPieceType() {
        return piece;
    }

    @Override
    public boolean makeMove() {
        Piece piece = getBoard().getPiecesInHand().take(getPieceType(), isSente());
        piece.setSquare(getTargetSquare());
        getTargetSquare().setPiece(piece);
        piece.setInHand(false);
        long timeStamp = TimeUtils.getCurrentTime();
        if (isSente()) {
            getBoard().setLastDropTimeStampSente(timeStamp);
        } else {
            getBoard().setLastDropTimeStampGote(timeStamp);
        }
        super.setTimestamp(timeStamp);
        return false;
    }

    @Override
    public void undoMove() {
        Piece piece = targetSquare.getPiece();
        piece.setInHand(true);
        piece.setSquare(null);
        piece.clearLegalMoves();
        piece.setPromoted(false);
        getTargetSquare().setPiece(null);
        getBoard().getPiecesInHand().add(piece);
        super.setTimestamp(0);
    }

    @Override
    public boolean offCooldown() {
        long lastMoved = isSente() ? getBoard().getLastDropTimeStampSente() : getBoard().getLastDropTimeStampGote();
        return lastMoved + Board.cooldown < TimeUtils.getCurrentTime();
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        DropMove move = (DropMove) o;
        return piece == move.piece && getTargetSquare() == move.getTargetSquare();
    }

    @Override
    protected String getDisambiguationJP () {

        // Find all other pieces of the same type to disambiguate.
        Set<Piece> piecesOfSameType = getBoard().getPiecesOnBoard().stream()
                .filter(p -> p.getPieceEnum() == piece && p.isSente() == isSente()).collect(Collectors.toSet());
        piecesOfSameType.removeIf(p -> p.getLegalMoves().isEmpty());

        if (!piecesOfSameType.isEmpty()) {
            Set<Square> targetSquares = new HashSet<>();
            for (Piece p : piecesOfSameType) {
                targetSquares.addAll(p.getLegalMoves().stream().map(m -> m.targetSquare).toList());
            }
            if (targetSquares.contains(targetSquare)) {
                return "打";
            }
        }
        return "";
    }
}
