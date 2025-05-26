package moe.nekoworks.shogi_backend.shogi;

public class AbstractSquare {
    protected final int x;
    protected final int y;

    public AbstractSquare(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public boolean isPromotionZone(boolean isSente) {
        return isSente ? y < 3 : y > 5;
    }

    public String getSquareNameInt() {
        char file = (char) ('9' - x);
        char rank = (char) ('a' + y);
        return String.valueOf(file) + rank;
    }

    public String getSquareNameJP() {
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
        return String.valueOf(file) + rank;
    }

    @Override
    public String toString() {
        return getSquareNameJP();
    }
}
