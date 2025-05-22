package moe.nekoworks.shogi_backend.shogi.piece;

import moe.nekoworks.shogi_backend.shogi.move.Move;
import moe.nekoworks.shogi_backend.shogi.move.MovementClass;

public enum PieceEnum {

    FU    ( "p", "Fu", "歩", "歩兵", "Pawn", MovementClass.FU),
    TO    ( "p", "To", "と", "と金", "Promoted Pawn", MovementClass.KIN),
    KYOU  ("+l", "kyou", "香", "香車", "Lance", MovementClass.KYOU),
    PKYOU ("+l", "nari kyo", "杏", "成香", "Promoted Lance", MovementClass.KIN),
    KEI   ( "n", "kei", "桂", "桂馬", "Knight", MovementClass.KEI),
    PKEI  ("+n", "nari kei", "圭", "成桂", "Promoted Knight", MovementClass.KIN),
    GIN   ( "s", "gin", "銀", "銀将", "Silver General", MovementClass.GIN),
    PGIN  ("+s", "nari gin", "全", "成銀", "Promoted Silver General", MovementClass.KIN),
    KIN   ( "g", "kin", "金", "金将", "Gold General", MovementClass.KIN),
    KAKU  ( "b", "kaku", "角", "角行", "Bishop", MovementClass.KAKU),
    UMA   ("+b", "uma", "馬", "竜馬", "Promoted Bishop", MovementClass.UMA),
    HI    ( "r", "hi", "飛", "飛車", "Rook", MovementClass.HI),
    RYUU  ("+r", "ryuu", "龍", "龍王", "Promoted Rook", MovementClass.RYUU),
    GYOKU ( "k", "gyouku", "玉", "玉将", "King", MovementClass.OU),
    OU    ( "k", "ou", "王", "王将", "King", MovementClass.OU);

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
