package moe.nekoworks.shogi_backend.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import moe.nekoworks.shogi_backend.shogi.Board;
import moe.nekoworks.shogi_backend.shogi.move.AbstractMove;
import moe.nekoworks.shogi_backend.shogi.move.BoardMove;
import moe.nekoworks.shogi_backend.shogi.move.DropMove;

import java.util.*;

@JsonIgnoreProperties(value = {"board"})
public class Game {

    private final String gameId;
    private final Board board;
    private String player1; // sente
    private String player2; // gote

    public Game() {
        board = new Board();
        gameId = UUID.randomUUID().toString();
    }

    public String getGameId() {
        return gameId;
    }

    public Board getBoard() {
        return board;
    }

    public String getPlayer1() {
        return player1;
    }

    public void setPlayer1(String player1) {
        this.player1 = player1;
    }

    public String getPlayer2() {
        return player2;
    }

    public void setPlayer2(String player2) {
        this.player2 = player2;
    }

    @JsonProperty("sfen")
    public String[] sfen() {
        return board.getBoardSfen();
    }

    @JsonProperty("lastMove")
    public Object lastMove() {
        if (board.getMovesPlayed().isEmpty()) {
            return null;
        }
        AbstractMove lastMove = board.getLastMove();
        if (lastMove.getClass() == BoardMove.class) {
            return new Move((BoardMove) lastMove);
        } else if (lastMove.getClass() == DropMove.class) {
            return new Drop((DropMove) lastMove);

        }
        return null;
    }

    public Map<String, ArrayList<String>> legalMoves(boolean isSente) {
        Iterator<Move> moveIterator = board.getBoardMoves(isSente).stream().map(Move::new).iterator();
        HashMap<String, ArrayList<String>> moveMap = new HashMap<>();
        while (moveIterator.hasNext()) {
            Move m = moveIterator.next();
            if (moveMap.containsKey(m.getOrig())) {
                moveMap.get(m.getOrig()).add(m.getDest());
            } else {
                ArrayList<String> dests = new ArrayList<>();
                dests.add(m.getDest());
                moveMap.put(m.getOrig(), dests);
            }
        }
        return moveMap;
    }

    private static final String[] SG_ROLE = {"pawn", "lance", "knight", "silver", "gold", "bishop", "rook"};

    public Map<String, ArrayList<String>> legalDrops(boolean isSente) {
        Iterator<Drop> dropIterator = board.getDropMoves(isSente).stream().map(Drop::new).iterator();
        HashMap<String, ArrayList<String>> dropMap = new HashMap<>();
        for (String piece : SG_ROLE) {
            dropMap.put("sente " + piece, new ArrayList<>());
            dropMap.put("gote " + piece, new ArrayList<>());
        }
        while (dropIterator.hasNext()) {
            Drop d = dropIterator.next();
            ArrayList<String> arr = dropMap.get(d.getPiece().toString());
            arr.add(d.getKey().toString());
        }
        return dropMap;
    }
}
