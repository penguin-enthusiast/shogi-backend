package moe.nekoworks.shogi_backend.shogi.move;

import moe.nekoworks.shogi_backend.shogi.Square;
import moe.nekoworks.shogi_backend.shogi.piece.Piece;
import moe.nekoworks.shogi_backend.shogi.piece.PieceEnum;

import java.util.*;
import java.util.stream.Collectors;

public class BoardMove extends Move {

    private final Piece piece;
    private final boolean isPromotion;

    public BoardMove(Piece piece, Square targetSquare, boolean isPromotion) {
        super(targetSquare);
        this.piece = piece;
        this.isPromotion = isPromotion;
    }

    public BoardMove(Piece piece, Square targetSquare) {
        this(piece, targetSquare, false);
    }

    public Piece getPiece() {
        return piece;
    }

    public boolean isPromotion() {
        return isPromotion;
    }

    public boolean isSente() {
        return piece.isSente();
    }

    @Override
    public PieceEnum getPieceType () {
        return piece.getPieceEnum();
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

    // Very complicated disambiguation rules for japanese notation.
    // see: https://en.wikipedia.org/wiki/Shogi_notation#Ambiguity_resolution:_Movement_description
    @Override
    protected String getDisambiguationJP() {
        // Find all other pieces of the same type to disambiguate.
        Set<Piece> piecesOfSameType = getBoard().getPiecesOnBoard().stream()
                .filter(p -> p.getPieceEnum() == piece.getPieceEnum()).collect(Collectors.toSet());
        piecesOfSameType.remove(piece);
        piecesOfSameType.removeIf(p -> p.isSente() != piece.isSente() || !p.getLegalMoves().stream()
                // For the piece p, get the set of all Squares it can move to
                .map(Move::getTargetSquare)
                .collect(Collectors.toSet())
                .contains(targetSquare));
        // The set piecesOfSameType should now only contain pieces of the same type as the one
        // currently being moved that can also move to the square being moved to.
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
