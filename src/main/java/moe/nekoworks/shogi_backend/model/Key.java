package moe.nekoworks.shogi_backend.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import moe.nekoworks.shogi_backend.shogi.AbstractSquare;

import java.util.Objects;

public class Key {

    // model for representing Key types in shogiground
    private final Rank rank;
    private final File file;

    public Key(Rank rank, File file) {
        this.rank = rank;
        this.file = file;
    }

    public AbstractSquare convertToSquare() {
        return new AbstractSquare(file.getXcoord(), rank.getYCoord());
    }

    public static Key convertSquareToKey(AbstractSquare square) {
        return new Key(Rank.getRankFromYCoord(square.getY()), File.getFileFromXCoord(square.getX()));
    }

    public static Key convertStringToKey(String s) {
        return new Key(Rank.getRankFromChar(s.charAt(1)), File.getFileFromChar(s.charAt(0)));
    }

    @Override
    public String toString() {
        return file.file + rank.rank;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Key key = (Key) o;
        return rank == key.rank && file == key.file;
    }

    @Override
    public int hashCode() {
        return Objects.hash(rank, file);
    }

    public enum Rank {

        A ("a"),
        B ("b"),
        C ("c"),
        D ("d"),
        E ("e"),
        F ("f"),
        G ("g"),
        H ("h"),
        I ("i");

        private final String rank;

        Rank(String rank) {
            this.rank = rank;
        }

        public String getRank() {
            return rank;
        }

        public int getYCoord() {
            return switch (this) {
                case A -> 0;
                case B -> 1;
                case C -> 2;
                case D -> 3;
                case E -> 4;
                case F -> 5;
                case G -> 6;
                case H -> 7;
                case I -> 8;
            };
        }

        public static Rank getRankFromYCoord(int y) {
            return switch (y) {
                case 0 -> A;
                case 1 -> B;
                case 2 -> C;
                case 3 -> D;
                case 4 -> E;
                case 5 -> F;
                case 6 -> G;
                case 7 -> H;
                case 8 -> I;
                default -> throw new IllegalArgumentException();
            };
        }

        public static Rank getRankFromChar(char c) {
            return getRankFromYCoord(Character.toLowerCase(c) - 97);
        }
    }

    public enum File {

        FILE_1 ("1"),
        FILE_2 ("2"),
        FILE_3 ("3"),
        FILE_4 ("4"),
        FILE_5 ("5"),
        FILE_6 ("6"),
        FILE_7 ("7"),
        FILE_8 ("8"),
        FILE_9 ("9");

        private final String file;

        File(String file) {
            this.file = file;
        }

        public String getFile() {
            return file;
        }

        public int getXcoord() {
            return switch (this) {
                case FILE_1 -> 8;
                case FILE_2 -> 7;
                case FILE_3 -> 6;
                case FILE_4 -> 5;
                case FILE_5 -> 4;
                case FILE_6 -> 3;
                case FILE_7 -> 2;
                case FILE_8 -> 1;
                case FILE_9 -> 0;
            };
        }

        public static File getFileFromXCoord(int x) {
            return switch (x) {
                case 0 -> FILE_9;
                case 1 -> FILE_8;
                case 2 -> FILE_7;
                case 3 -> FILE_6;
                case 4 -> FILE_5;
                case 5 -> FILE_4;
                case 6 -> FILE_3;
                case 7 -> FILE_2;
                case 8 -> FILE_1;
                default -> throw new IllegalArgumentException();
            };
        }

        public static File getFileFromChar(char c) {
            return getFileFromXCoord(57 - c);
        }
    }
}
