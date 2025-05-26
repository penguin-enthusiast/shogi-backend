package moe.nekoworks.shogi_backend.shogi;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import moe.nekoworks.shogi_backend.shogi.piece.Piece;

@JsonIgnoreProperties(value = {"board", "piece", "squareNameJP", "squareNameInt"})
public class Square extends AbstractSquare {

    private final Board board;

    private Piece piece = null;

    public Square(Board board, int x, int y) {
        super(x, y);
        this.board = board;
    }

    public Board getBoard() {
        return board;
    }

    public Piece getPiece() {
        return piece;
    }

    public void setPiece(Piece piece) {
        this.piece = piece;
    }

}