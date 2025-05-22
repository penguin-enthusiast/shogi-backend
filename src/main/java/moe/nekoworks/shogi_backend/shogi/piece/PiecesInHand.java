package moe.nekoworks.shogi_backend.shogi.piece;


import java.util.ArrayDeque;
import java.util.Deque;
import java.util.EmptyStackException;
import java.util.HashSet;
import java.util.Set;

public class PiecesInHand {

    private final Deque<Piece> fuSente = new ArrayDeque<>(9);
    private final Deque<Piece> fuGote = new ArrayDeque<>(9);
    private final Deque<Piece> kyouSente = new ArrayDeque<>(4);
    private final Deque<Piece> kyouGote = new ArrayDeque<>(4);
    private final Deque<Piece> keiSente = new ArrayDeque<>(4);
    private final Deque<Piece> keiGote = new ArrayDeque<>(4);
    private final Deque<Piece> ginSente = new ArrayDeque<>(4);
    private final Deque<Piece> ginGote = new ArrayDeque<>(4);
    private final Deque<Piece> kinSente = new ArrayDeque<>(4);
    private final Deque<Piece> kinGote = new ArrayDeque<>(4);
    private final Deque<Piece> kakuSente = new ArrayDeque<>(2);
    private final Deque<Piece> kakuGote = new ArrayDeque<>(2);
    private final Deque<Piece> hiSente = new ArrayDeque<>(2);
    private final Deque<Piece> hiGote = new ArrayDeque<>(2);

    public void add(Piece p) {
        if (p.isSente()) {
            switch (p.getPieceEnum()) {
                case FU -> fuSente.push(p);
                case KYOU -> kyouSente.push(p);
                case KEI -> keiSente.push(p);
                case GIN -> ginSente.push(p);
                case KIN -> kinSente.push(p);
                case KAKU -> kakuSente.push(p);
                case HI -> hiSente.push(p);
            }
        } else {
            switch (p.getPieceEnum()) {
                case FU -> fuGote.push(p);
                case KYOU -> kyouGote.push(p);
                case KEI -> keiGote.push(p);
                case GIN -> ginGote.push(p);
                case KIN -> kinGote.push(p);
                case KAKU -> kakuGote.push(p);
                case HI -> hiGote.push(p);
            }
        }
    }

    public Piece take(PieceEnum pe, boolean isSente) {
        Piece p = null;
        try {
            if (isSente) {
                switch (pe) {
                    case FU -> p = fuSente.pop();
                    case KYOU -> p = kyouSente.pop();
                    case KEI -> p = keiSente.pop();
                    case GIN -> p = ginSente.pop();
                    case KIN -> p = kinSente.pop();
                    case KAKU -> p = kakuSente.pop();
                    case HI -> p = hiSente.pop();
                }
            } else {
                switch (pe) {
                    case FU -> p = fuGote.pop();
                    case KYOU -> p = kyouGote.pop();
                    case KEI -> p = keiGote.pop();
                    case GIN -> p = ginGote.pop();
                    case KIN -> p = kinGote.pop();
                    case KAKU -> p = kakuGote.pop();
                    case HI -> p = hiGote.pop();
                }
            }
            return p;
        } catch (EmptyStackException e) {
            return null;
        }
    }

    // Returns the number of each piece in hand in a 2 x 7 array.
    // The first row is for sente, second row is for gote.
    // The indicies hold the amount of each piece in the following order:
    // [0, 1, 2, 3, 4, 5, 6]
    //  P  L  N  S  G  B  R
    public int[][] getPieces () {
        int[][] pieces = new int[2][7];

        pieces[0][0] = fuSente.size();
        pieces[0][1] = kyouSente.size();
        pieces[0][2] = keiSente.size();
        pieces[0][3] = ginSente.size();
        pieces[0][4] = kinSente.size();
        pieces[0][5] = kakuSente.size();
        pieces[0][6] = hiSente.size();

        pieces[1][0] = fuGote.size();
        pieces[1][1] = kyouGote.size();
        pieces[1][2] = keiGote.size();
        pieces[1][3] = ginGote.size();
        pieces[1][4] = kinGote.size();
        pieces[1][5] = kakuGote.size();
        pieces[1][6] = hiGote.size();

        return pieces;
    }

    public Set<PieceEnum> possibleDrops (boolean isSente) {
        Set<PieceEnum> pieces = new HashSet<>();
        if (isSente) {
            if (!fuSente.isEmpty()) pieces.add(PieceEnum.FU);
            if (!kyouSente.isEmpty()) pieces.add(PieceEnum.KYOU);
            if (!keiSente.isEmpty()) pieces.add(PieceEnum.KEI);
            if (!ginSente.isEmpty()) pieces.add(PieceEnum.GIN);
            if (!kinSente.isEmpty()) pieces.add(PieceEnum.KIN);
            if (!kakuSente.isEmpty()) pieces.add(PieceEnum.KAKU);
            if (!hiSente.isEmpty()) pieces.add(PieceEnum.HI);
        } else {
            if (!fuGote.isEmpty()) pieces.add(PieceEnum.FU);
            if (!kyouGote.isEmpty()) pieces.add(PieceEnum.KYOU);
            if (!keiGote.isEmpty()) pieces.add(PieceEnum.KEI);
            if (!ginGote.isEmpty()) pieces.add(PieceEnum.GIN);
            if (!kinGote.isEmpty()) pieces.add(PieceEnum.KIN);
            if (!kakuGote.isEmpty()) pieces.add(PieceEnum.KAKU);
            if (!hiGote.isEmpty()) pieces.add(PieceEnum.HI);
        }
        return pieces;
    }
}
