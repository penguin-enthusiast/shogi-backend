package moe.nekoworks.shogi_backend.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import moe.nekoworks.shogi_backend.shogi.piece.Piece;
import moe.nekoworks.shogi_backend.shogi.piece.PieceEnum;

public class SGPiece {

    private final PieceEnum piece;
    private final boolean isSente;

    public SGPiece(PieceEnum piece, boolean isSente) {
        this.piece = piece;
        this.isSente = isSente;
    }

    public SGPiece(@JsonProperty("role") String role, @JsonProperty("color") String color) {
        this.piece = PieceEnum.getPieceFromSgRole(role);
        this.isSente = color.equals("sente");
    }

    public SGPiece(Piece piece) {
        this.piece = piece.getPieceEnum();
        this.isSente = piece.isSente();
    }

    public String getRole() {
        return piece.getSgRole();
    }

    public String getColor() {
        return isSente ? "sente" : "gote";
    }

    @Override
    public String toString() {
        return getColor() + " " + getRole();
    }
}
