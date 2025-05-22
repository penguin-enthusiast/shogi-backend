package moe.nekoworks.shogi_backend.shogi.piece;

import moe.nekoworks.shogi_backend.shogi.Board;
import moe.nekoworks.shogi_backend.shogi.move.BoardMove;
import moe.nekoworks.shogi_backend.shogi.Square;
import moe.nekoworks.shogi_backend.shogi.move.MoveHelper;
import moe.nekoworks.shogi_backend.shogi.move.MovementClass;
import org.apache.commons.lang3.tuple.Pair;

import java.util.HashSet;
import java.util.Set;

public abstract class Piece {

    private Square square;
    private boolean isSente;
    private boolean inHand = false;
    private double lastMoved = 0;
    private Set<BoardMove> legalMoves = new HashSet<>();

    public Piece(boolean isSente) {
        this.isSente = isSente;
        this.square = null;
        this.inHand = true;
    }

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
        square = null;
        legalMoves.clear();
    }

    public Square getSquare() {
        return square;
    }

    // should only ever be called by the move() method
    public void setSquare(Square square) {
        this.square = square;
    }

    public Set<BoardMove> getLegalMoves () {
        return legalMoves;
    }

    public boolean inPromotionZone() {
        if (square == null) {
            return false;
        }
        return square.isPromotionZone(isSente);
    }

    public abstract PieceEnum getPieceEnum();

    public Set<BoardMove> updateLegalMoves (Board board) {
        Set<BoardMove> moves = new HashSet<>();
        if (inHand) {
            return moves;
        }
        MovementClass movementClass = getPieceEnum().getMovementClass();
        byte[][] movementMap = movementClass.getMovementMap(isSente);
        for (int y = 0; y < movementClass.getSizeY(); y++) {
            for (int x = 0; x < movementClass.getSizeX(); x++) {
                Pair<Integer, Integer> moveClassOrigin = movementClass.getOrigin(isSente);
                assert moveClassOrigin != null;
                int xOffset = moveClassOrigin.getLeft() - x;
                int yOffset = moveClassOrigin.getRight() - y;
                if (movementMap[y][x] == 2) {
                    Square targetSquare = board.getSquare(square, xOffset, yOffset);
                    if (targetSquare != null) {
                        createMove(board, this, targetSquare, moves);
                    }
                } else if (movementMap[y][x] == 3) {
                    boolean moveAdded;
                    int targetX = 0, targetY = 0;
                    do {
                        targetX += xOffset;
                        targetY += yOffset;
                        Square targetSquare = board.getSquare(square, targetX, targetY);
                        if (targetSquare == null) {
                            moveAdded = false;
                        } else {
                            moveAdded = createMove(board, this, targetSquare, moves);
                        }
                    } while (moveAdded);
                }
            }
        }
        legalMoves = moves;
        return legalMoves;
    }

    protected boolean createMove(Board board, Piece piece, Square targetSquare, Set<BoardMove> moves) {
        return MoveHelper.createMove(board, this, targetSquare, moves, false);
    }

    public String getName() {
        return getPieceEnum().getNameJPShort();
    }

    @Override
    public String toString() {
        return getName();
    }
}
