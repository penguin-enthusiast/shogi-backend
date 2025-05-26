package moe.nekoworks.shogi_backend.model;

import java.util.ArrayList;
import java.util.Map;

// The response from the server after sending a move.
// Basic implementation of a pair that returns the last move made, and the resulting set of legal moves.
public class MovesResponse {

    private final Move lastMove;
    private final Map<String, ArrayList<String>> moveMap;

    public MovesResponse(Move lastMove, Map<String, ArrayList<String>> moveMap) {
        this.lastMove = lastMove;
        this.moveMap = moveMap;
    }

    public Move getLastMove() {
        return lastMove;
    }

    public Map<String, ArrayList<String>> getMoveMap() {
        return moveMap;
    }
}
