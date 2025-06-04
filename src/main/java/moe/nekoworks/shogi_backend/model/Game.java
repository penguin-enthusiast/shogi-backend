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
    private final String player1; // sente
    private String player2; // gote
    private GameStatus status;
    private boolean player1Ready = false;
    private boolean player2Ready = false;

    public Game(String player1) {
        board = new Board();
        gameId = UUID.randomUUID().toString();
        this.player1 = player1;
        status = GameStatus.WAITING;
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

    public String getPlayer2() {
        return player2;
    }

    public GameStatus getStatus() {
        return status;
    }

    public boolean isPlayer1Ready() {
        return player1Ready;
    }

    public void setPlayer1Ready(boolean player1Ready) {
        this.player1Ready = player1Ready;
    }

    public boolean isPlayer2Ready() {
        return player2Ready;
    }

    public void setPlayer2Ready(boolean player2Ready) {
        this.player2Ready = player2Ready;
    }

    public void joinGame(String player2) {
        this.player2 = player2;
    }

    public void startGame() {
        status = GameStatus.IN_PROGRESS;
    }

    public void finishGame() {
        status = GameStatus.FINISHED;
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
            arr.add(d.getKey());
        }
        return dropMap;
    }
}
