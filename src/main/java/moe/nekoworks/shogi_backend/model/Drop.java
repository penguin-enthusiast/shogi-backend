package moe.nekoworks.shogi_backend.model;

import moe.nekoworks.shogi_backend.shogi.move.DropMove;

public class Drop {

    private final SGPiece piece;
    private final Key key;

    public Drop(SGPiece piece, Key key) {
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

    public Key getKey() {
        return key;
    }
}
