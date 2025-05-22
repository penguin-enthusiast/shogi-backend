package moe.nekoworks.shogi_backend.shogi.piece;

public enum PieceEnum {

    FU    ( "p", "Fu", "歩", "歩兵", "Pawn"),
    TO    ( "p", "To", "と", "と金", "Promoted Pawn"),
    KYOU  ("+l", "kyou", "香", "香車", "Lance"),
    PKYOU ("+l", "nari kyo", "杏", "成香", "Promoted Lance"),
    KEI   ( "n", "kei", "桂", "桂馬", "Knight"),
    PKEI  ("+n", "nari kei", "圭", "成桂", "Promoted Knight"),
    GIN   ( "s", "gin", "銀", "銀将", "Silver General"),
    PGIN  ("+s", "nari gin", "全", "成銀", "Promoted Silver General"),
    KIN   ( "g", "kin", "金", "金将", "Gold General"),
    KAKU  ( "b", "kaku", "角", "角行", "Bishop"),
    UMA   ("+b", "uma", "馬", "竜馬", "Promoted Bishop"),
    HI    ( "r", "hi", "飛", "飛車", "Rook"),
    RYUU  ("+r", "ryuu", "龍", "龍王", "Promoted Rook"),
    GYOKU ( "k", "gyouku", "玉", "玉将", "King"),
    OU    ( "k", "ou", "王", "王将", "King");

    private final String symbol;
    private final String nameRomaShort;
    private final String nameJPShort;
    private final String nameJPLong;
    private final String nameInt;

    PieceEnum(String symbol, String nameRomaShort, String nameJPShort, String nameJPLong, String nameInt) {
        this.symbol = symbol;
        this.nameRomaShort = nameRomaShort;
        this.nameJPShort = nameJPShort;
        this.nameJPLong = nameJPLong;
        this.nameInt = nameInt;
    }

    public String getSymbol() {
        return symbol;
    }

    public String getNameRomaShort() {
        return nameRomaShort;
    }

    public String getNameJPShort() {
        return nameJPShort;
    }

    public String getNameJPLong() {
        return nameJPLong;
    }

    public String getNameInt() {
        return nameInt;
    }
}
