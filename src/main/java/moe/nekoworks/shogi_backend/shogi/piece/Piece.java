package moe.nekoworks.shogi_backend.shogi.piece;

import moe.nekoworks.shogi_backend.shogi.Board;
import moe.nekoworks.shogi_backend.shogi.Move;
import moe.nekoworks.shogi_backend.shogi.Square;

import java.util.HashSet;
import java.util.Set;

public abstract class Piece {

    private Square square;
    private boolean isSente;
    private boolean inHand = false;
    private double lastMoved = 0;

    protected Set<Move> legalMoves = new HashSet<>();

    public Piece (Square square, boolean isSente) {
        this.square = square;
        this.isSente = isSente;
        square.setPiece(this);
    }

    public double getLastMoved() {
        return lastMoved;
    }

    public void setLastMoved(double lastMoved) {
        this.lastMoved = lastMoved;
    }

    public boolean isSente() {
        return isSente;
    }

    public void setSente(boolean sente) {
        isSente = sente;
    }

    public boolean isInHand() {
        return inHand;
    }

    public void putInHand() {
        if(inHand) {
            return;
        }
        inHand = true;
        isSente = !isSente;
        legalMoves.clear();
    }

    public Square getSquare() {
        return square;
    }

    // should only ever be called by the move() method
    public void setSquare(Square square) {
        this.square = square;
    }

    public Set<Move> getLegalMoves () {
        return legalMoves;
    }

    public String getName() {
        return getPieceEnum().getNameJPShort();
    }

    // A helper method that creates a move from the piece to another square based on the x and y offset from the originating piece.
    // This only validates whether the destination square exists, and is not occupied by a friendly piece.
    // The calling method should ensure that the move is valid for the piece
    protected boolean createMove(Board board, int xOffset, int yOffset, Set<Move> moves, boolean isSente, boolean allowPromotion) {
        Square s = board.getSquare(xOffset, yOffset);
        if (s != null && (s.getPiece() == null || s.getPiece().isSente() != isSente)) {
            switch(s.getPiece().getPieceEnum()) {
                case FU, KYOU:
                    if (isSente? s.getY() == 0 : s.getY() == 8) {
                        break;
                    }
                case KEI:
                    if (isSente? s.getY() <= 1 : s.getY() >= 7) {
                        break;
                    }
                default:
                    moves.add(new Move(this, s));
            }
            if (s.isPromotionZone(isSente) && allowPromotion) {
                moves.add(new Move(this, s, true));
            }
            return true;
        }
        return false;
    }

    // Gold moves are so common, we give it its own helper method
    protected Set<Move> getGoldMoves(Board board) {
        // moves like
        //  O  O  O    .  O  .
        //  O  ☗  O    O  ⛊  O
        //  .  O  .    O  O  O
        HashSet<Move> moves = new HashSet<>();
        int x = getSquare().getX();
        int y = getSquare().getY();
        Square s;

        createMove(board, x + 1, y, moves, isSente, false);
        createMove(board, x - 1, y, moves, isSente, false);
        createMove(board, x, y + 1, moves, isSente, false);
        createMove(board, x, y - 1, moves, isSente, false);

        y = isSente ? y - 1 : y + 1;

        createMove(board, x + 1, y, moves, isSente, false);
        createMove(board, x - 1, y, moves, isSente, false);

        return moves;
    }

    public abstract PieceEnum getPieceEnum();

    public abstract Set<Move> updateLegalMoves(Board board);
}
