package moe.nekoworks.shogi_backend.shogi.move;

import moe.nekoworks.shogi_backend.shogi.Square;
import moe.nekoworks.shogi_backend.shogi.piece.Piece;
import moe.nekoworks.shogi_backend.shogi.piece.PieceEnum;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

public class DropMove extends Move {

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
    public void makeMove() {
        Piece piece = getBoard().getPiecesInHand().take(getPieceType(), isSente());
        piece.setSquare(getTargetSquare());
        getTargetSquare().setPiece(piece);
        piece.setInHand(false);
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
