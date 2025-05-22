package moe.nekoworks.shogi_backend.shogi;

import moe.nekoworks.shogi_backend.shogi.piece.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Board {

    public static final String INITIAL_STATE_MSFEN = "lnsgkgsnl/1r5b1/ppppppppp/9/9/9/PPPPPPPPP/1B5R1/LNSGKGSNL - 1";

    private static final Logger log = LoggerFactory.getLogger(Board.class);
    private int move;

    private Square[][] board;

    public Board() {
        initBoard();
    }

    public void initBoard() {
        initBoard(INITIAL_STATE_MSFEN);
    }

    public void initBoard(String msfen) {
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


        int rank = 0;
        int file = 0;

        // pieces
        boolean isPromoted = false;
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
                    file += c - '0' - 1;
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
                    isPromoted = false;
                    break;
                case 'L':
                    p = new Kyousha(board[rank][file], isSente, isPromoted);
                    isPromoted = false;
                    break;
                case 'N':
                    p = new Keima(board[rank][file], isSente, isPromoted);
                    isPromoted = false;
                    break;
                case 'S':
                    p = new Ginshou(board[rank][file], isSente, isPromoted);
                    isPromoted = false;
                    break;
                case 'G':
                    p = new Kinshou(board[rank][file], isSente);
                    isPromoted = false;
                    break;
                case 'B':
                    p = new Kakugyou(board[rank][file], isSente, isPromoted);
                    isPromoted = false;
                    break;
                case 'R':
                    p = new Hisha(board[rank][file], isSente, isPromoted);
                    isPromoted = false;
                    break;
                case 'K':
                    // give sente the gyo by default
                    p = isSente ? new Gyokushou(board[rank][file], isSente) : new Oushou(board[rank][file], isSente);
                    isPromoted = false;
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
            }
            if (c != '/') {
                file++;
            }
        }
    }

    public String printBoardJP() {
        final String row = "-------------------------------------------------------\n";
        StringBuilder printedBoard = new StringBuilder(578);
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
        return printedBoard.toString();
    }

    @Override
    public String toString() {
        return printBoardJP();
    }

    public static boolean inPromotionZone(boolean isSente, Square square) {
        int rank = square.getY();
        return isSente ? rank < 3 : rank > 5;
    }

}
