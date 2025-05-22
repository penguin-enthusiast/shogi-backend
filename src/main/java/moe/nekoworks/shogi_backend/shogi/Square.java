package moe.nekoworks.shogi_backend.shogi;

import moe.nekoworks.shogi_backend.shogi.piece.Piece;

import java.awt.geom.PathIterator;
import java.io.IOException;

public class Square {

    private final int x;
    private final int y;

    private Piece piece = null;

    public Square(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public Piece getPiece() {
        return piece;
    }

    public void setPiece(Piece piece) {
        this.piece = piece;
    }

    public boolean isPromotionZone(boolean isSente) {
        return isSente ? y < 3 : y > 5;
    }

    public String getSquareNameInt() {
        char file = (char) ('9' - x);
        char rank = (char) ('a' + y);
        return String.valueOf(file) + rank;
    }

    public String getSquareNameJp() {
        char file = (char) ('9' - x);
        char rank = switch (y) {
            case 0 -> '一';
            case 1 -> '二';
            case 2 -> '三';
            case 3 -> '四';
            case 4 -> '五';
            case 5 -> '六';
            case 6 -> '七';
            case 7 -> '八';
            case 8 -> '九';
            default -> ' ';
        };
        ;
        return String.valueOf(file) + rank;
    }

    @Override
    public String toString () {
        return getSquareNameJp();
    }
}