package moe.nekoworks.shogi_backend.shogi.move;

import moe.nekoworks.shogi_backend.shogi.Board;
import moe.nekoworks.shogi_backend.shogi.Square;
import moe.nekoworks.shogi_backend.shogi.piece.Piece;
import moe.nekoworks.shogi_backend.shogi.piece.PieceEnum;
import moe.nekoworks.shogi_backend.shogi.piece.PromotablePiece;

import java.util.BitSet;
import java.util.HashSet;
import java.util.Set;

public class MoveHelper {

    // A helper method that creates a move from the piece to another square based on the x and y offset from the originating piece.
    // This only validates whether the destination square exists, and is not occupied by a friendly piece.
    // The calling method should ensure that the move is valid for the piece
    public static boolean createMove(Piece piece, Square targetSquare, Set<BoardMove> moves, boolean allowPromotion) {
        boolean isSente = piece.isSente();
        if (targetSquare.getPiece() == null || targetSquare.getPiece().isSente() != isSente) {
            int y = targetSquare.getY();
            switch(piece.getPieceEnum()) {
                case KEI:
                    if (isSente? y <= 1 : y >= 7) {
                        break;
                    }
                case FU, KYOU:
                    if (isSente? y == 0 : y == 8) {
                        break;
                    }
                default:
                    moves.add(new BoardMove(piece, targetSquare));
            }
            if (isPromoteable(piece, targetSquare) && allowPromotion) {
                moves.add(new BoardMove(piece, targetSquare, true));
            }
            return (targetSquare.getPiece() == null);
        }
        return false;
    }

    public static boolean isPromoteable(Piece piece, Square targetSquare) {
        if (!(piece instanceof PromotablePiece)) {
            return false;
        }
        return !piece.isPromoted() && (targetSquare.isPromotionZone(piece.isSente()) || piece.getSquare().isPromotionZone(piece.isSente()));
    }

    public static Set<DropMove> createDropMoves(Board board, PieceEnum piece, boolean isSente) {
        Set<DropMove> moves = new HashSet<>();

        BitSet columns = new BitSet(9);
        // cannot drop a pawn on a file that already has a friendly pawn
        if (piece == PieceEnum.FU) {
            for (Piece p : board.getPiecesOnBoard()) {
                if (p.getPieceEnum() == PieceEnum.FU && p.isSente() == isSente) {
                    columns.set(p.getSquare().getX());
                }
            }
        }
        for (int x = 0; x < 9; x++) {
            if (!columns.get(x)) {
                for (int y = 0; y < 9; y++) {
                    // Certain pieces can only move forward. To prevent players from dropping a piece,
                    // that isn't able to move, we check that the Y-coordinate is valid for the piece.
                    boolean validY;
                    switch (piece) {
                        case FU, KYOU -> validY = isSente ? y > 0 : y < 8;
                        case KEI -> validY = isSente ? y > 1 : y < 7;
                        default -> validY = true;
                    }
                    if (validY && board.getSquare(x, y).getPiece() == null) {
                        DropMove m = new DropMove(board.getSquare(x, y), piece, isSente);
                        moves.add(m);
                    }
                }
            }
        }
        return  moves;
    }

}
