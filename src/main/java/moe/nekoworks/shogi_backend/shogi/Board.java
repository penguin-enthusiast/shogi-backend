package moe.nekoworks.shogi_backend.shogi;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import moe.nekoworks.shogi_backend.exception.GameException;
import moe.nekoworks.shogi_backend.exception.MoveException;
import moe.nekoworks.shogi_backend.misc.TimeUtils;
import moe.nekoworks.shogi_backend.shogi.move.BoardMove;
import moe.nekoworks.shogi_backend.shogi.move.DropMove;
import moe.nekoworks.shogi_backend.shogi.move.AbstractMove;
import moe.nekoworks.shogi_backend.shogi.move.MoveHelper;
import moe.nekoworks.shogi_backend.shogi.piece.*;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;

import java.util.*;

@JsonIgnoreProperties(value = {"board", "pieces", "movesPlayed", "lastMoveWithNotation"})
public class Board {

    public static final String INITIAL_STATE_MSFEN = "lnsgkgsnl/1r5b1/ppppppppp/9/9/9/PPPPPPPPP/1B5R1/LNSGKGSNL - - 1";
    // piece cooldown in milliseconds
    public static final long cooldown = 5000;
    private static final char[] PIECE_NAMES_JP = {'歩', '香', '桂', '銀', '金', '角', '飛'};

    private final Square[][] board;
    private final Set<Piece> pieces;
    private final PiecesInHand piecesInHand = new PiecesInHand();
    private final LinkedList<Pair<AbstractMove, String>> movesPlayed = new LinkedList<>();
    @JsonBackReference
    private final Set<AbstractMove> legalMoves = new HashSet<>();

    private long startTimeStamp;
    private long lastDropTimeStampSente;
    private long lastDropTimeStampGote;

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
                board[y][x] = new Square(this, x, y);
            }
        }

        // parse msfen
        String[] fen = msfen.split(" ");
        if (fen.length != 4 && fen.length != 3) {
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

        updateLegalMoves();
    }

    public Square getSquare(AbstractSquare square) {
        return getSquare(square.x, square.y);
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

    public PiecesInHand getPiecesInHand() {
        return piecesInHand;
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
        Set<PieceEnum> pieces = piecesInHand.possibleDrops(isSente);
        Set<DropMove> moves = new HashSet<>();
        for (PieceEnum p : pieces) {
            moves.addAll(MoveHelper.createDropMoves(this, p, isSente));
        }
        return moves;
    }

    public Set<DropMove> getDropMoves() {
        Set<DropMove> drops = new HashSet<>();
        drops.addAll(getDropMoves(false));
        drops.addAll(getDropMoves(true));
        return drops;
    }

    public LinkedList<Pair<AbstractMove, String>> getMovesPlayed() {
        return new LinkedList<>(movesPlayed);
    }

    public AbstractMove getLastMove() {
        if (movesPlayed.isEmpty()) {
            return null;
        }
        return movesPlayed.getLast().getLeft();
    }

    public Pair<AbstractMove, String> getLastMoveWithNotation() {
        if (movesPlayed.isEmpty()) {
            return null;
        }
        return movesPlayed.getLast();
    }

    public Set<AbstractMove> getLegalMoves() {
        return Collections.unmodifiableSet(legalMoves);
    }

    public long getStartTimeStamp() {
        return startTimeStamp;
    }

    public long getLastDropTimeStampSente() {
        return lastDropTimeStampSente;
    }

    public void setLastDropTimeStampSente(long lastDropTimeStampSente) {
        this.lastDropTimeStampSente = lastDropTimeStampSente;
    }

    public long getLastDropTimeStampGote() {
        return lastDropTimeStampGote;
    }

    public void setLastDropTimeStampGote(long lastDropTimeStampGote) {
        this.lastDropTimeStampGote = lastDropTimeStampGote;
    }

    private void updateLegalMoves() {
        for (Piece p : getPiecesOnBoard()) {
            p.updateLegalMoves(this);
        }
        legalMoves.clear();
        legalMoves.addAll(getBoardMoves(true));
        legalMoves.addAll(getBoardMoves(false));
        legalMoves.addAll(getDropMoves(true));
        legalMoves.addAll(getDropMoves(false));
    }

    // returns whether the move captures a king
    public boolean commitMove(AbstractMove move) {
        if (move == null) {
            throw new GameException("Illegal move.");
        }

        if (!move.offCooldown()) {
            throw new MoveException("Piece on cooldown");
        }

        if (legalMoves.contains(move)) {
            boolean kingCapture = move.makeMove();
            movesPlayed.add(new ImmutablePair<>(move, move.toString()));
            updateLegalMoves();
            return kingCapture;
        }
        throw new GameException("Illegal move.");
    }

    public boolean undoLastMove() {
        if (movesPlayed.isEmpty()) {
            return false;
        }

        AbstractMove lastMove = getLastMove();
        lastMove.undoMove();
        movesPlayed.removeLast();
        updateLegalMoves();
        return true;
    }

    public void startGame() {
        long timeStamp = TimeUtils.getCurrentTime();
        startTimeStamp = timeStamp;
        for (Piece p : getPiecesOnBoard()) {
            p.setLastMoved(timeStamp);
        }
    }

    public String printBoardInt() {
        final String topRow = "┌─────┬─────┬─────┬─────┬─────┬─────┬─────┬─────┬─────┐\n";
        final String midRow = "├─────┼─────┼─────┼─────┼─────┼─────┼─────┼─────┼─────┤\n";
        final String botRow = "└─────┴─────┴─────┴─────┴─────┴─────┴─────┴─────┴─────┘\n";
        StringBuilder printedBoard = new StringBuilder();
        int[][] piecesInHand = getPiecesInHand().getPieces();
        printPiecesInHand(piecesInHand[1], printedBoard);
        for (int i = 0; i < 9; i++) {
            if (i == 0) {
                printedBoard.append(topRow);
            } else {
                printedBoard.append(midRow);
            }
            printedBoard.append('│');
            for (int j = 0; j < 9; j++) {
                printedBoard.append(' ');
                Piece p = board[i][j].getPiece();
                if (p == null) {
                    printedBoard.append("   ");
                } else {
                    if (!p.isPromoted()) {
                        printedBoard.append(" ");
                    }
                    printedBoard.append(board[i][j].getPiece().toString());
                    printedBoard.append(p.isSente() ? '^' : 'v');
                }
                printedBoard.append(' ');
                printedBoard.append('│');
            }
            printedBoard.append('\n');
        }
        printedBoard.append(botRow);
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

    public String[] getBoardSfen() {
        String[] sfen = new String[3];

        StringBuilder piecesOnBoard = new StringBuilder();
        for (int rank = 0; rank < 9; rank ++) {
            int emptySquares = 0;
            for (int file = 0; file < 9; file ++) {
                if (board[rank][file].getPiece() == null) {
                    emptySquares++;
                    if (file == 8) {
                        piecesOnBoard.append(emptySquares);
                    }
                } else {
                    if (emptySquares != 0) {
                        piecesOnBoard.append(emptySquares);
                        emptySquares = 0;
                    }
                    piecesOnBoard.append(board[rank][file].getPiece().getSymbol(true));
                }
            }
            if (rank != 8) {
                piecesOnBoard.append('/');
            }
        }
        sfen[0] = piecesOnBoard.toString();

        StringBuilder piecesInHand = new StringBuilder();
        int[][] hands = getPiecesInHand().getPieces();
        for (int i = 0; i < hands[0].length; i++) {
            if (hands[0][i] != 0) { // sente
                if (hands[0][i] > 1) {
                    piecesInHand.append(hands[0][i]);
                }
                piecesInHand.append(PieceEnum.getPieceType(i).getSymbol().toUpperCase());
            }
            if (hands[1][i] != 0) { // gote
                if (hands[1][i] > 1) {
                    piecesInHand.append(hands[1][i]);
                }
                piecesInHand.append(PieceEnum.getPieceType(i).getSymbol().toLowerCase());
            }
        }
        if (piecesInHand.isEmpty()) {
            sfen[1] = "-";
        } else {
            sfen[1] = piecesInHand.toString();
        }

        sfen[2] = Integer.toString(movesPlayed.size() + 1);

        return sfen;
    }

    @Override
    public String toString() {
        return printBoardInt();
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

}
