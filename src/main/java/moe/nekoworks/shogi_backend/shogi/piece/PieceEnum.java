package moe.nekoworks.shogi_backend.shogi.piece;

import moe.nekoworks.shogi_backend.shogi.move.MovementClass;

public enum PieceEnum {

    FU    ("P", "Fu", "歩", "歩兵", "Pawn", MovementClass.FU),
    TO    ("+P", "To", "と", "と金", "Promoted Pawn", MovementClass.KIN),
    KYOU  ("L", "kyou", "香", "香車", "Lance", MovementClass.KYOU),
    PKYOU ("+L", "nari kyo", "杏", "成香", "Promoted Lance", MovementClass.KIN),
    KEI   ("N", "kei", "桂", "桂馬", "Knight", MovementClass.KEI),
    PKEI  ("+N", "nari kei", "圭", "成桂", "Promoted Knight", MovementClass.KIN),
    GIN   ("S", "gin", "銀", "銀将", "Silver General", MovementClass.GIN),
    PGIN  ("+S", "nari gin", "全", "成銀", "Promoted Silver General", MovementClass.KIN),
    KIN   ("G", "kin", "金", "金将", "Gold General", MovementClass.KIN),
    KAKU  ("B", "kaku", "角", "角行", "Bishop", MovementClass.KAKU),
    UMA   ("+B", "uma", "馬", "竜馬", "Promoted Bishop", MovementClass.UMA),
    HI    ("R", "hi", "飛", "飛車", "Rook", MovementClass.HI),
    RYUU  ("+R", "ryuu", "龍", "龍王", "Promoted Rook", MovementClass.RYUU),
    GYOKU ("K", "gyouku", "玉", "玉将", "King", MovementClass.OU),
    OU    ("K", "ou", "王", "王将", "King", MovementClass.OU);

    private final String symbol;
    private final String nameRomaShort;
    private final String nameJPShort;
    private final String nameJPLong;
    private final String nameInt;
    private final MovementClass movementClass;

    PieceEnum(String symbol, String nameRomaShort, String nameJPShort, String nameJPLong, String nameInt, MovementClass movementClass) {
        this.symbol = symbol;
        this.nameRomaShort = nameRomaShort;
        this.nameJPShort = nameJPShort;
        this.nameJPLong = nameJPLong;
        this.nameInt = nameInt;
        this.movementClass = movementClass;
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

    public MovementClass getMovementClass() {
        return movementClass;
    }
}
