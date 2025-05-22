package moe.nekoworks.shogi_backend;

import moe.nekoworks.shogi_backend.shogi.Board;
import moe.nekoworks.shogi_backend.shogi.Square;
import moe.nekoworks.shogi_backend.shogi.move.BoardMove;
import moe.nekoworks.shogi_backend.shogi.move.DropMove;
import moe.nekoworks.shogi_backend.shogi.move.Move;
import moe.nekoworks.shogi_backend.shogi.piece.PieceEnum;

import java.util.Scanner;

public class MainTestClass {

    public static void main(String[] args) {
        Board board = new Board();
        System.out.println(board);

        Scanner sc = new Scanner(System.in);
        String s = "";
        int turn = 1;
        while (!s.equals("exit")) {
            s = sc.nextLine();
            Move m = createMove(board, s, turn);
            if (board.commitMove(m)) {
                System.out.println(board.getLastMove().getRight() + "     move: " + turn);
                //System.out.println(board);
                turn++;
            }
        }
    }
    // for testing only
    // parse a move from a string
    // For a move on the board, the string must be 4 numbers representing the
    // origin X coord, origin Y coord, target X coord, target Y coord, respectively.
    // Coordinates follow a standard shogi board, 9 for the left most file, and bottom most rank.
    // an option '+' is appended to indicate a promotion
    // For a drop, the move starts with 'S' or 'G' depending on the side, a letter for the piece
    // 'P' 'L' 'K' 'S' 'G' 'B' 'R', then 2 numbers for the X and Y coordinates of the target square
    public static Move createMove(Board b, String _s, int turn) {
        String s = _s.toLowerCase();
        char firstChar = s.charAt(0);
        if (Character.isAlphabetic(s.charAt(0))) {
            if (s.length() < 4) {
                return null;
            }
            boolean isSente = (turn % 2) == 1;
            PieceEnum pieceEnum = null;
            switch (s.charAt(0)) {
                case 'p' -> pieceEnum = PieceEnum.FU;
                case 'l' -> pieceEnum = PieceEnum.KYOU;
                case 'n' -> pieceEnum = PieceEnum.KEI;
                case 's' -> pieceEnum = PieceEnum.GIN;
                case 'g' -> pieceEnum = PieceEnum.KIN;
                case 'b' -> pieceEnum = PieceEnum.KAKU;
                case 'r' -> pieceEnum = PieceEnum.HI;
            }
            if (pieceEnum == null) {
                return null;
            }
            assert s.charAt(1) == '*';
            if (!Character.isDigit(s.charAt(2)) || (!Character.isDigit(s.charAt(3)) && !Character.isAlphabetic(s.charAt(3)))) {
                return null;
            }
            int x = 9 - Character.digit(s.charAt(2), 10);
            int y;
            if (!Character.isAlphabetic(s.charAt(3))) {
                y = Character.digit(s.charAt(3), 10) -1;
            } else {
                y = s.charAt(3) - 'a';
            }
            if (b.getSquare(x, y) == null) {
                return null;
            }
            return new DropMove(b.getSquare(x, y), pieceEnum, isSente);
        } else if (Character.isDigit(firstChar) && firstChar != '0') {
            if (s.length() < 4) {
                return null;
            }
            if (!Character.isDigit(s.charAt(0)) || (!Character.isDigit(s.charAt(1)) && !Character.isAlphabetic(s.charAt(1))) || !Character.isDigit(s.charAt(2)) || (!Character.isDigit(s.charAt(3)) && !Character.isAlphabetic(s.charAt(3)))) {
                return null;
            }
            int xOrigin = 9 - Character.digit(s.charAt(0), 10);
            int yOrigin;
            if (!Character.isAlphabetic(s.charAt(1))) {
                yOrigin = Character.digit(s.charAt(1), 10) - 1;
            } else {
                yOrigin = s.charAt(1) - 'a';
            }
            int xTarget = 9 - Character.digit(s.charAt(2), 10);
            int yTarget;
            if (!Character.isAlphabetic(s.charAt(3))) {
                yTarget = Character.digit(s.charAt(3), 10) -1;
            } else {
                yTarget = s.charAt(3) - 'a';
            }
            Square origin = b.getSquare(xOrigin, yOrigin);
            Square target = b.getSquare(xTarget, yTarget);
            if (origin == null || target == null || origin.getPiece() == null) {
                return null;
            }
            boolean promote = (s.length() >= 5 && s.charAt(4) == '+');
            return new BoardMove(origin.getPiece(), target, promote);
        }
        return null;
    }

}
