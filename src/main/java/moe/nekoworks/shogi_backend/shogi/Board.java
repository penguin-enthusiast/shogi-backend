package moe.nekoworks.shogi_backend.shogi;

import moe.nekoworks.shogi_backend.shogi.move.BoardMove;
import moe.nekoworks.shogi_backend.shogi.move.DropMove;
import moe.nekoworks.shogi_backend.shogi.move.Move;
import moe.nekoworks.shogi_backend.shogi.move.MoveHelper;
import moe.nekoworks.shogi_backend.shogi.piece.*;

import java.util.*;

public class Board {

    public static final String INITIAL_STATE_MSFEN = "lnsgkgsnl/1r5b1/ppppppppp/9/9/9/PPPPPPPPP/1B5R1/LNSGKGSNL - 1";

    private static final char[] PIECE_NAMES_JP = {'歩', '香', '桂', '銀', '金', '角', '飛'};

    private final Square[][] board;
    private final Set<Piece> pieces;
    private final PiecesInHand piecesInHand = new PiecesInHand();
    private final Set<Move> moves = new HashSet<>();

    public Board() {
        this(INITIAL_STATE_MSFEN);
    }

    public Board (String msfen) throws IllegalArgumentException {
        // here we use a modified version of the SFEN notation to set a board.
        // each piece is followed by a floating point number <1 starting with a '.'
        // representing the 'cooldown'. a '.' by itself or no '.' represents a 0 value.
        // it also ignores the fields for the player to move
        // TODO this only parses regular SFEN, update to satisfy above requirements, also add validation

        // init board with all the squares
        board = new Square[9][9];
        for (int y = 8; y >= 0; y--) {
            for (int x = 0; x < 9; x++) {
                board[y][x] = new Square(x, y);
            }
        }

        // parse msfen
        String[] fen = msfen.split(" ");
        if (fen.length != 4) {
            throw new IllegalArgumentException();
        }

        // pieces
        int rank = 0;
        int file = 0;
        boolean isPromoted = false;
        pieces = new HashSet<>();
        for (int i = 0; i < fen[0].length(); i++) {
            Piece p = null;
            char c = fen[0].charAt(i);
            boolean isSente = true;
            if (Character.isAlphabetic(c)) {
                if (Character.isLowerCase(c)) {
                    c = Character.toUpperCase(c);
                    isSente = false;
                }
            }
            switch(c) {
                case '1','2','3','4','5','6','7','8','9':
                    file += c - '0';
                    break;
                case '/':
                    rank++;
                    file = 0;
                    break;
                case '+':
                    isPromoted = true;
                    break;
                case 'P':
                    p = new Fuhyou(board[rank][file], isSente, isPromoted);
                    break;
                case 'L':
                    p = new Kyousha(board[rank][file], isSente, isPromoted);
                    break;
                case 'N':
                    p = new Keima(board[rank][file], isSente, isPromoted);
                    break;
                case 'S':
                    p = new Ginshou(board[rank][file], isSente, isPromoted);
                    break;
                case 'G':
                    p = new Kinshou(board[rank][file], isSente);
                    break;
                case 'B':
                    p = new Kakugyou(board[rank][file], isSente, isPromoted);
                    break;
                case 'R':
                    p = new Hisha(board[rank][file], isSente, isPromoted);
                    break;
                case 'K':
                    // give sente the gyo by default
                    p = isSente ? new Gyokushou(board[rank][file], true) : new Oushou(board[rank][file], false);
                    break;
                case '.':
                    // TODO implement timing
                    break;
                default:
                    // throw new IOException();
                    // TODO implement validation and exception handling
                    break;
            }

            if (p != null) {
                pieces.add(p);
                isPromoted = false;
                file++;
            }
        }

        // side to move
        // this isn't actually necessary, just for compatibility

        // pieces in hand
        int count = 1;
        for (int i = 0; i < fen[2].length(); i++) {
            char c = fen[2].charAt(i);
            boolean isSente = true;
            if (Character.isAlphabetic(c)) {
                if (Character.isLowerCase(c)) {
                    c = Character.toUpperCase(c);
                    isSente = false;
                }
            }
            Piece p = null;
            switch (c) {
                case '1','2','3','4','5','6','7','8','9':
                    count = c - '1' + 1;
                    break;
                case 'P':
                    for (int j = 0; j < count; j++) {
                        p = new Fuhyou(isSente);
                        pieces.add(p);
                        piecesInHand.add(p);
                    }
                    break;
                case 'L':
                    for (int j = 0; j < count; j++) {
                        p = new Kyousha(isSente);
                        pieces.add(p);
                        piecesInHand.add(p);
                    }
                    break;
                case 'N':
                    for (int j = 0; j < count; j++) {
                        p = new Keima(isSente);
                        pieces.add(p);
                        piecesInHand.add(p);
                    }
                    break;
                case 'S':
                    for (int j = 0; j < count; j++) {
                        p = new Ginshou(isSente);
                        pieces.add(p);
                        piecesInHand.add(p);
                    }
                    break;
                case 'G':
                    for (int j = 0; j < count; j++) {
                        p = new Kinshou(isSente);
                        pieces.add(p);
                        piecesInHand.add(p);
                    }
                    break;
                case 'B':
                    for (int j = 0; j < count; j++) {
                        p = new Kakugyou(isSente);
                        pieces.add(p);
                        piecesInHand.add(p);
                    }
                    break;
                case 'R':
                    for (int j = 0; j < count; j++) {
                        p = new Hisha(isSente);
                        pieces.add(p);
                        piecesInHand.add(p);
                    }
                    break;
            }
            if (p != null) {
                count = 1;
            }
        }

        updateMoves();
    }

    public Square getSquare(int x, int y) {
        if (0 <= x && x <= 8 && 0 <= y && y <= 8) {
            return board[y][x];
        }
        return null;
    }

    public Square getSquare(Square s, int xOffset, int yOffset) {
        int x = s.getX() + xOffset;
        int y = s.getY() + yOffset;
        if (0 <= x && x <= 8 && 0 <= y && y <= 8) {
            return board[y][x];
        }
        return null;
    }

    public int[][] getPiecesInHand() {
        return piecesInHand.getPieces();
    }

    public Set<Piece> getPiecesOnBoard() {
        HashSet<Piece> pieces = new HashSet<>();
        for (Piece p : this.pieces) {
            if (!p.isInHand()) {
                pieces.add(p);
            }
        }
        return pieces;
    }

    public Set<BoardMove> getBoardMoves(boolean isSente) {
        Set<BoardMove> moves = new HashSet<>();
        for (Piece p : getPiecesOnBoard()) {
            if (p.isSente() == isSente) {
                moves.addAll(p.getLegalMoves());
            }
        }
        return moves;
    }

    public Set<DropMove> getDropMoves(boolean isSente) {
        System.out.println("DEBUG Board.getDropMoves");
        Set<PieceEnum> pieces = piecesInHand.possibleDrops(isSente);
        Set<DropMove> moves = new HashSet<>();
        for (PieceEnum p : pieces) {
            System.out.println("getting drops - " + (isSente ? "sente " : "gote ") + " - " + p.getNameJPShort());
            moves.addAll(MoveHelper.createDropMoves(this, p, isSente));
        }
        return moves;
    }

    public Set<Move> moves() {
        return moves;
    }

    private void updateMoves () {
        for (Piece p : getPiecesOnBoard()) {
            p.updateLegalMoves(this);
        }
        moves.clear();
        moves.addAll(getBoardMoves(true));
        moves.addAll(getBoardMoves(false));
        moves.addAll(getDropMoves(true));
        moves.addAll(getDropMoves(false));
    }

    public boolean commitMove (Move move) {
        if (move == null) {
            return false;
        }
        if (BoardMove.class == move.getClass()) {
            return commitMove((BoardMove) move);
        } else if (DropMove.class == move.getClass()) {
            return commitMove((DropMove) move);
        }
        return false;
    }

    public boolean commitMove(BoardMove move) throws IllegalArgumentException {
        if (move == null || move.getPiece() == null) {
            throw new IllegalArgumentException();
        }
        final Piece piece = move.getPiece();
        Set<BoardMove> moves = getBoardMoves(piece.isSente());
        Piece targetPiece = move.getTargetSquare().getPiece();
        if (moves.contains(move)) {
            // make the move;
            if (targetPiece != null) {
                piecesInHand.add(targetPiece);
                targetPiece.putInHand();
            }
            piece.setSquare(move.getTargetSquare());
            move.getTargetSquare().setPiece(piece);
        } else {
            return false;
        }
        updateMoves();
        return true;
    }

    public boolean commitMove(DropMove move) {
        return false;
    }

    public String printBoardJP() {
        final String row = "-------------------------------------------------------\n";
        StringBuilder printedBoard = new StringBuilder();
        int[][] piecesInHand = getPiecesInHand();
        printPiecesInHand(piecesInHand[1], printedBoard);
        for (int i = 0; i < 9; i++) {
            printedBoard.append(row);
            printedBoard.append('|');
            for (int j = 0; j < 9; j++) {
                printedBoard.append(' ');
                Piece p = board[i][j].getPiece();
                if (p == null) {
                    printedBoard.append(" . ");
                } else {
                    printedBoard.append(p.isSente() ? '^' : 'v');
                    printedBoard.append(board[i][j].getPiece().getName());
                }
                printedBoard.append(' ');
                printedBoard.append('|');
            }
            printedBoard.append('\n');
        }
        printedBoard.append(row);
        printPiecesInHand(piecesInHand[0], printedBoard);
        return printedBoard.toString();
    }

    private void printPiecesInHand(int[] pieces, StringBuilder sb) {
        for (int i = 0; i < 7; i++) {
            if (pieces[i] != 0) {
                sb.append(' ');
                sb.append(pieces[i]);
                sb.append('x');
                sb.append(PIECE_NAMES_JP[i]);
            }
        }
        sb.append('\n');
    }

    @Override
    public String toString() {
        return printBoardJP();
    }

    // test method to visualize a piece's move. remove in the future
    public String printAttackingSquares(Piece p) {
        StringBuilder sb = new StringBuilder();
        sb.append(p).append(" - ").append(p.getSquare()).append('\n');
        Set<BoardMove> moves = p.getLegalMoves();
        HashSet<Square> squaresPromotion = new HashSet<>();
        HashSet<Square> squaresNoPromotion = new HashSet<>();
        for (BoardMove m : moves) {
            if (m.isPromotion()) {
                squaresPromotion.add(m.getTargetSquare());
            } else {
                squaresNoPromotion.add(m.getTargetSquare());
            }
        }

        for (int i = 0; i < 9; i++) {
            for (int j = 0; j < 9; j++) {
                if (squaresNoPromotion.contains(board[i][j])) {
                    sb.append(" x ");
                }
                else {
                    sb.append(" . ");
                }
            }
            if (!squaresPromotion.isEmpty()) {
                sb.append("   ");
                for (int j = 0; j < 9; j++) {
                    if (squaresPromotion.contains(board[i][j])) {
                        sb.append(" + ");
                    } else {
                        sb.append(" . ");
                    }
                }
            }
            sb.append('\n');
        }
        return sb.toString();
    }

    // data structure to store pieces in hand
    static class PiecesInHand {

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

}
