package moe.nekoworks.shogi_backend.shogi.move;

import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;

/*
 * A map describing movements of pieces represented as a 5x5 array of ints.
 * A 1 the piece, a 2 represents a possible move,
 * a 3 adjacent to the piece represents a ranging move (traverses in a line),
 * with 0's everywhere else.
 */
public enum MovementClass {

    FU(new byte[][]{
            {1},
            {2}
    }, new byte[][]{
            {2},
            {1}
    }),
    KEI(new byte[][]{
            {0, 1, 0},
            {0, 0, 0},
            {2, 0, 2},
    }, new byte[][]{
            {2, 0, 2},
            {0, 0, 0},
            {0, 1, 0},
    }),
    KYOU(new byte[][]{
            {1},
            {3}
    }, new byte[][]{
            {3},
            {1}
    }),
    GIN(new byte[][]{
            {2, 0, 2},
            {0, 1, 0},
            {2, 2, 2}
    }, new byte[][]{
            {2, 2, 2},
            {0, 1, 0},
            {2, 0, 2}
    }),
    KIN(new byte[][]{
            {0, 2, 0},
            {2, 1, 2},
            {2, 2, 2}
    }, new byte[][]{
            {2, 2, 2},
            {2, 1, 2},
            {0, 2, 0}
    }),
    KAKU(new byte[][]{
            {3, 0, 3},
            {0, 1, 0},
            {3, 0, 3}
    }),
    UMA(new byte[][]{
            {3, 2, 3},
            {2, 1, 2},
            {3, 2, 3}
    }),
    HI(new byte[][]{
            {0, 3, 0},
            {3, 1, 3},
            {0, 3, 0}
    }),
    RYUU(new byte[][]{
            {2, 3, 2},
            {3, 1, 3},
            {2, 3, 2}
    }),
    OU(new byte[][]{
            {2, 2, 2},
            {2, 1, 2},
            {2, 2, 2}
    });

    private final byte[][] movementMapSente;
    private final byte[][] movementMapGote;



    MovementClass (byte[][] movementMapSente, byte[][] movementMapGote) {
        this.movementMapSente = movementMapSente;
        this.movementMapGote = movementMapGote;
    }

    MovementClass (byte[][] movementMapSente) {
        this.movementMapSente = movementMapSente;
        this.movementMapGote = movementMapSente;
    }

    public byte[][] getMovementMap (boolean isSente) {
        return isSente ? movementMapSente : movementMapGote;
    }

    public Pair<Integer, Integer> getOrigin (boolean isSente) {
        byte[][] map = isSente ? movementMapSente : movementMapGote;
        for (int y = 0; y < getSizeY(); y++) {
            for (int x = 0; x < getSizeX(); x++) {
                if (map[y][x] == 1) {
                    return new ImmutablePair<>(x, y);
                }
            }
        }
        return null;
    }

    public int getSizeX () {
        return movementMapSente[0].length;
    }

    public int getSizeY () {
        return movementMapSente.length;
    }

}
