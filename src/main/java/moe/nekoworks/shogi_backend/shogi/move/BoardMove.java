package moe.nekoworks.shogi_backend.shogi.move;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import moe.nekoworks.shogi_backend.misc.TimeUtils;
import moe.nekoworks.shogi_backend.shogi.Board;
import moe.nekoworks.shogi_backend.shogi.Square;
import moe.nekoworks.shogi_backend.shogi.piece.Piece;
import moe.nekoworks.shogi_backend.shogi.piece.PieceEnum;

import java.util.*;
import java.util.stream.Collectors;

@JsonIgnoreProperties(value = {"piece", "capturedPiece"})
public class BoardMove extends AbstractMove {

    private final Square originSquare;
    private final Piece piece;
    private final Piece capturedPiece;
    private final boolean isPromotion;
    private final boolean isCapture;
    private final boolean capturedPieceIsPromoted;

    public BoardMove(Piece piece, Square targetSquare, boolean isPromotion) {
        super(targetSquare, piece.isSente());
        this.piece = piece;
        this.originSquare = piece.getSquare();
        this.isPromotion = isPromotion && MoveHelper.isPromoteable(piece, targetSquare);
        if (targetSquare.getPiece() != null) {
            isCapture = true;
            capturedPiece = targetSquare.getPiece();
            capturedPieceIsPromoted = targetSquare.getPiece().isPromoted();
        } else {
            isCapture = false;
            capturedPiece = null;
            capturedPieceIsPromoted = false;
        }
    }

    public BoardMove(Piece piece, Square targetSquare) {
        this(piece, targetSquare, false);
    }

    public Square getOriginSquare() {
        return originSquare;
    }

    public Piece getPiece() {
        return piece;
    }

    public Piece getCapturedPiece() {
        return capturedPiece;
    }

    public boolean isPromotion() {
        return isPromotion;
    }

    public boolean isCapture() {
        return isCapture;
    }

    @Override
    public boolean isKingCapture() {
        return isCapture() && (capturedPiece.getPieceEnum() == (isSente() ? PieceEnum.OU : PieceEnum.GYOKU));
    }

    private boolean isCapturedPieceIsPromoted() {
        return capturedPieceIsPromoted;
    }

    @Override
    public PieceEnum getPieceType () {
        return piece.getPieceEnum();
    }

    @Override
    public void makeMove() {
        Board board = getBoard();
        if (isCapture()) {
            Piece capturedPiece = getCapturedPiece();
            capturedPiece.setInHand(true);
            capturedPiece.setSente(isSente());
            capturedPiece.clearLegalMoves();
            capturedPiece.setPromoted(false);
            getTargetSquare().setPiece(null);
            board.getPiecesInHand().add(capturedPiece);
        }
        getOriginSquare().setPiece(null);
        getTargetSquare().setPiece(getPiece());
        getPiece().setSquare(getTargetSquare());
        if (isPromotion()) {
            getPiece().setPromoted(true);
        }
        long timeStamp = TimeUtils.getCurrentTime();
        piece.setLastMoved(timeStamp);
        super.setTimestamp(timeStamp);
    }

    @Override
    public void undoMove() {
        Board board = getBoard();
        if (isPromotion()) {
            getPiece().setPromoted(false);
        }
        getTargetSquare().setPiece(null);
        getOriginSquare().setPiece(getPiece());
        getPiece().setSquare(getOriginSquare());
        if (isCapture()) {
            Piece capturedPiece = board.getPiecesInHand().take(getCapturedPiece().getPieceEnum(), isSente());
            capturedPiece.setInHand(false);
            capturedPiece.setSente(!isSente());
            capturedPiece.setPromoted(isCapturedPieceIsPromoted());
            capturedPiece.setSquare(targetSquare);
            getTargetSquare().setPiece(capturedPiece);
        }
        super.setTimestamp(0);

    }

    @Override
    public boolean offCooldown() {
        return piece.getLastMoved() + Board.cooldown < TimeUtils.getCurrentTime();
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        BoardMove boardMove = (BoardMove) o;
        return isPromotion == boardMove.isPromotion && piece == boardMove.piece && targetSquare == boardMove.targetSquare;
    }

    @Override
    public int hashCode() {
        return Objects.hash(piece, targetSquare, isPromotion);
    }

    @Override
    public String notationInt() {
        return (isSente() ? '☗' : '☖') +
                (isPromotion ? getPieceType().getSymbol().substring(1) : getPieceType().getSymbol()) +
                (isAmbiguous() ? getOriginSquare().toString() : "") +
                (isCapture ? 'x' : "") +
                getTargetSquare().toString() +
                (isPromotion ? '+' : "");
    }

    // Very complicated disambiguation rules for japanese notation.
    // see: https://en.wikipedia.org/wiki/Shogi_notation#Ambiguity_resolution:_Movement_description
    // For the current move, find all pieces of the same type that can move to the same target square
    @Override
    protected Set<Piece> getAmbiguousPieces() {
        // Find all other pieces of the same type to disambiguate.
        Set<Piece> piecesOfSameType = getBoard().getPiecesOnBoard().stream()
                .filter(p -> p.getPieceEnum() == piece.getPieceEnum()).collect(Collectors.toSet());
        piecesOfSameType.remove(piece);
        piecesOfSameType.removeIf(p -> p.isSente() != piece.isSente() || !p.getLegalMoves().stream()
                // For the piece p, get the set of all Squares it can move to
                .map(AbstractMove::getTargetSquare)
                .collect(Collectors.toSet())
                .contains(targetSquare));
        // The set piecesOfSameType should now only contain pieces of the same type as the one
        // currently being moved that can also move to the square being moved to.
        return piecesOfSameType;
    }

    @Override
    protected String getDisambiguationJP() {
        // Find all other pieces of the same type to disambiguate.
        Set<Piece> piecesOfSameType = getAmbiguousPieces();
        if (!piecesOfSameType.isEmpty()) {
            // Define booleans that represent movement direction relative to the piece and side,
            // hence the boolean operation with the piece's side.
            PieceMovement movement = new PieceMovement(piece.getSquare(), targetSquare);
            Map<Piece, PieceMovement> otherMovements = new HashMap<>();
            for (Piece p : piecesOfSameType) {
                otherMovements.put(p, new PieceMovement(p.getSquare(), targetSquare));
            }

            // begin disambiguation
            // try using 上 (upwards movement)
            boolean otherPiecesMovingUp = false;
            for (PieceMovement pm : otherMovements.values()) {
                if (pm.up) {
                    otherPiecesMovingUp = true;
                    break;
                }
            }
            boolean otherPiecesMovingDown = false;
            for (PieceMovement pm : otherMovements.values()) {
                if (pm.down) {
                    otherPiecesMovingDown = true;
                    break;
                }
            }
            boolean otherPiecesMovingLeft = false;
            for (PieceMovement pm : otherMovements.values()) {
                if (pm.left) {
                    otherPiecesMovingLeft = true;
                    break;
                }
            }
            boolean otherPiecesMovingRight = false;
            for (PieceMovement pm : otherMovements.values()) {
                if (pm.right) {
                    otherPiecesMovingRight = true;
                    break;
                }
            }
            boolean otherPiecesMovingHorizontal = false;
            for (PieceMovement pm : otherMovements.values()) {
                if ((pm.left || pm.right) && !pm.down && !pm.up) {
                    otherPiecesMovingHorizontal = true;
                    break;
                }
            }
            boolean movingDirectlyUp = movement.up && !movement.left && !movement.right;
            boolean movingHorizontal = (movement.left || movement.right) && !movement.down && !movement.up;

            if (movement.up && !otherPiecesMovingUp) {
                return "上" + appendPromotionStatus();
            }
            if (movement.down && !otherPiecesMovingDown) {
                return "引" + appendPromotionStatus();
            }
            if (movingHorizontal && !otherPiecesMovingHorizontal) {
                return "寄" + appendPromotionStatus();
            }
            if (movingDirectlyUp) {
                if (piece.getPieceEnum() == PieceEnum.RYUU || piece.getPieceEnum() == PieceEnum.UMA) {
                    // Is potentially insufficient for disambiguation if there are more than 2 pieces.
                    // However, a normal piece set only contains 2.
                    // Update in the future to account for different piece sets?
                    if (!otherPiecesMovingRight) {
                        return "左";
                    } else if (!otherPiecesMovingLeft) {
                        return "右";
                    }
                }
                return  "直";
            }
            if (movement.right) {
                if (otherPiecesMovingRight) {
                    if (movement.up) {
                        return "左上" + appendPromotionStatus();
                    } else if (movement.down) {
                        return "左引" + appendPromotionStatus();
                    } else {
                        return "左寄" + appendPromotionStatus();
                    }
                } else {
                    return "左" + appendPromotionStatus();
                }
            } else {
                if (otherPiecesMovingLeft) {
                    if (movement.up) {
                        return "右上" + appendPromotionStatus();
                    } else if (movement.down) {
                        return "右引" + appendPromotionStatus();
                    } else {
                        return "右寄" + appendPromotionStatus();
                    }
                } else {
                    return "右" + appendPromotionStatus();
                }

            }
        }
        return appendPromotionStatus();
    }

    private String appendPromotionStatus() {
        boolean isPromoteable = MoveHelper.isPromoteable(piece, targetSquare);
        if (isPromotion) {
            return "成";
        } else if (isPromoteable) {
            // piece can be promoted on this move but chooses not to
            return "不成";
        }
        return "";
    }

    private class PieceMovement {

        public boolean up, down, left, right;

        public PieceMovement(Square origin, Square target) {
            int xOffset = target.getX() - origin.getX();
            int yOffset = target.getY() - origin.getY();
            if (xOffset == 0) {
                left = false;
                right = false;
            } else {
                left = (xOffset < 0) ^ !piece.isSente();
                right = (xOffset > 0) ^ !piece.isSente();
            }
            if (yOffset == 0) {
                up = false;
                down = false;
            } else {
                up = (yOffset < 0) ^ !piece.isSente();
                down = (yOffset > 0) ^ !piece.isSente();
            }
        }
    }

}
