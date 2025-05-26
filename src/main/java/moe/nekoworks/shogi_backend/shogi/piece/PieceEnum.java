package moe.nekoworks.shogi_backend.shogi.piece;

import moe.nekoworks.shogi_backend.shogi.move.MovementClass;

public enum PieceEnum {

    FU    ("P", "Fu", "歩", "歩兵", "pawn", MovementClass.FU),
    TO    ("+P", "To", "と", "と金", "tokin", MovementClass.KIN),
    KYOU  ("L", "kyou", "香", "香車", "lance", MovementClass.KYOU),
    PKYOU ("+L", "nari kyo", "杏", "成香", "promotedlance", MovementClass.KIN),
    KEI   ("N", "kei", "桂", "桂馬", "knight", MovementClass.KEI),
    PKEI  ("+N", "nari kei", "圭", "成桂", "promotedknight", MovementClass.KIN),
    GIN   ("S", "gin", "銀", "銀将", "silver", MovementClass.GIN),
    PGIN  ("+S", "nari gin", "全", "成銀", "promotedsilver", MovementClass.KIN),
    KIN   ("G", "kin", "金", "金将", "gold", MovementClass.KIN),
    KAKU  ("B", "kaku", "角", "角行", "bishop", MovementClass.KAKU),
    UMA   ("+B", "uma", "馬", "竜馬", "horse", MovementClass.UMA),
    HI    ("R", "hi", "飛", "飛車", "rook", MovementClass.HI),
    RYUU  ("+R", "ryuu", "龍", "龍王", "dragon", MovementClass.RYUU),
    GYOKU ("K", "gyouku", "玉", "玉将", "king", MovementClass.OU),
    OU    ("K", "ou", "王", "王将", "king", MovementClass.OU);

    private final String symbol;
    private final String nameRomaShort;
    private final String nameJPShort;
    private final String nameJPLong;
    private final String sgRole;
    private final MovementClass movementClass;

    PieceEnum(String symbol, String nameRomaShort, String nameJPShort, String nameJPLong, String sgRole, MovementClass movementClass) {
        this.symbol = symbol;
        this.nameRomaShort = nameRomaShort;
        this.nameJPShort = nameJPShort;
        this.nameJPLong = nameJPLong;
        this.sgRole = sgRole;
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

    public String getSgRole() {
        return sgRole;
    }

    public static PieceEnum getPieceFromSgRole(String role) {
        return switch (role) {
            case "pawn" -> FU;
            case "tokin" -> TO;
            case "lance" -> KYOU;
            case "promotedlance" -> PKYOU;
            case "knight" -> KEI;
            case "promotedknight" -> PKEI;
            case "silver" -> GIN;
            case "promotedsilver" -> PGIN;
            case "gold" -> KIN;
            case "bishop" -> KAKU;
            case "horse" -> UMA;
            case "rook" -> HI;
            case "dragon" -> RYUU;
            case "king" -> OU;
            default -> throw new IllegalStateException("Unexpected value: " + role);
        };
    }

    public MovementClass getMovementClass() {
        return movementClass;
    }

    /*
     * 0 -> FU
     * 1 -> KYOU
     * 2 -> KEI
     * 3 -> GIN
     * 4 -> KIN
     * 5 -> KAKU
     * 6 -> HI
     *
     * easy way to get the piece type from array index when used with PiecesInHand.getPieces
     */
    public static PieceEnum getPieceType (int index) {
        return switch (index) {
            case 0 -> FU;
            case 1 -> KYOU;
            case 2 -> KEI;
            case 3 -> GIN;
            case 4 -> KIN;
            case 5 -> KAKU;
            case 6 -> HI;
            default -> throw new IllegalStateException("Unexpected value: " + index);
        };
    }
}
