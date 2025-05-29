package moe.nekoworks.shogi_backend.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import moe.nekoworks.shogi_backend.shogi.Board;
import moe.nekoworks.shogi_backend.shogi.Square;
import moe.nekoworks.shogi_backend.shogi.move.DropMove;
import moe.nekoworks.shogi_backend.shogi.piece.PieceEnum;

public class Drop {

    private final SGPiece piece;
    private final Key key;

    public Drop(@JsonProperty("piece") SGPiece piece, @JsonProperty("key") Key key) {
        this.piece = piece;
        this.key = key;
    }

    public Drop(DropMove dropMove) {
        this.piece = new SGPiece(dropMove.getPieceType(), dropMove.isSente());
        this.key = Key.convertSquareToKey(dropMove.getTargetSquare());
    }


    public SGPiece getPiece() {
        return piece;
    }

    public String getKey() {
        return key.toString();
    }

    public DropMove buildDrop(Board board) {
        Square targetSquare = board.getSquare(key.convertToSquare());
        PieceEnum pieceEnum = PieceEnum.getPieceFromSgRole(piece.getRole());
        boolean isSente = piece.getColor().equals("sente");
        return new DropMove(targetSquare, pieceEnum, isSente);
    }
}
