package moe.nekoworks.shogi_backend.shogi.piece;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import moe.nekoworks.shogi_backend.shogi.Board;
import moe.nekoworks.shogi_backend.shogi.move.BoardMove;
import moe.nekoworks.shogi_backend.shogi.Square;
import moe.nekoworks.shogi_backend.shogi.move.MoveHelper;
import moe.nekoworks.shogi_backend.shogi.move.MovementClass;
import org.apache.commons.lang3.tuple.Pair;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

@JsonIgnoreProperties(value = {"nameJPShort", "pieceEnum"})
public abstract class Piece {

    private Square square;
    private boolean isSente;
    private boolean inHand = false;
    private double lastMoved = 0;
    @JsonBackReference
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

    public void setInHand(boolean inHand) {
        this.inHand = inHand;
    }

    public Square getSquare() {
        return square;
    }

    // should only ever be called by the move() method
    public void setSquare(Square square) {
        this.square = square;
    }

    public Set<BoardMove> getLegalMoves() {
        return Collections.unmodifiableSet(legalMoves);
    }

    public abstract PieceEnum getPieceEnum();

    public void updateLegalMoves (Board board) {
        Set<BoardMove> moves = new HashSet<>();
        if (inHand) {
            return;
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
    }

    public void clearLegalMoves() {
        legalMoves.clear();
    }

    protected boolean createMove(Board board, Piece piece, Square targetSquare, Set<BoardMove> moves) {
        return MoveHelper.createMove(this, targetSquare, moves, false);
    }

    public boolean isPromoted() {
        return false;
    }

    public void setPromoted(boolean promoted) {
    }

    public String getNameJPShort() {
        return getPieceEnum().getNameJPShort();
    }

    public String getSymbol() {
        return getSymbol(false);
    }

    public String getSymbol(boolean differentiateSide) {
        if (differentiateSide) {
            if (isSente) {
                return getPieceEnum().getSymbol().toUpperCase();
            } else {
                return getPieceEnum().getSymbol().toLowerCase();
            }
        } else {
            return getPieceEnum().getSymbol();
        }
    }

    @Override
    public String toString() {
        return getSymbol();
    }
}
