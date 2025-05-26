package moe.nekoworks.shogi_backend.model;

import moe.nekoworks.shogi_backend.shogi.Board;

import java.util.UUID;

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
}
