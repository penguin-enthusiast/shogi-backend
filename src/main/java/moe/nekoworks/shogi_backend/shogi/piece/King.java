package moe.nekoworks.shogi_backend.shogi.piece;

import moe.nekoworks.shogi_backend.shogi.Board;
import moe.nekoworks.shogi_backend.shogi.move.BoardMove;
import moe.nekoworks.shogi_backend.shogi.Square;
import moe.nekoworks.shogi_backend.shogi.move.MoveHelper;

import java.util.HashSet;
import java.util.Set;

public abstract class King extends Piece {

    public King(Square square, boolean isSente) {
        super(square, isSente);
    }

}
