package moe.nekoworks.shogi_backend.service;

import moe.nekoworks.shogi_backend.exception.GameException;
import moe.nekoworks.shogi_backend.model.Drop;
import moe.nekoworks.shogi_backend.model.Game;
import moe.nekoworks.shogi_backend.model.Move;
import moe.nekoworks.shogi_backend.repository.GameRepository;
import moe.nekoworks.shogi_backend.shogi.move.BoardMove;
import moe.nekoworks.shogi_backend.shogi.move.DropMove;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Map;

@Service
public class GameService {

    private final GameRepository gameRepository;

    public GameService(GameRepository gameRepository) {
        this.gameRepository = gameRepository;
    }

    public Game createGame(String playerId) {
        Game game = new Game();
        game.setPlayer1(playerId);
        gameRepository.save(game);
        return game;
    }

    public Game joinGame(String playerId, String gameID) {
        Game game = getGameByID(gameID);
        if (game.getPlayer2() != null) {
            throw new GameException("Someone already joined this game.");
        }
        game.setPlayer2(playerId);
        return game;
    }

    public Game connectToRandomGame(String playerId) {
        Game game = gameRepository.findAvailableGame();
        game.setPlayer2(playerId);
        return game;
    }

    public boolean makeDrop(String gameId, String playerId, Drop drop) {
        Game game = getGameByID(gameId);
//            if (game.getPlayer1() == null || game.getPlayer2() == null) {
//                throw new GameException("The game hasn't started yet.");
//            }
        boolean isSente;
        if (playerId.equals(game.getPlayer1())) {
            isSente = true;
        } else if (playerId.equals(game.getPlayer2())) {
            isSente = false;
        } else {
            throw new GameException("Client making move must be a player.");
        }
        DropMove dropMove = drop.buildDrop(game.getBoard());
        if (isSente == dropMove.isSente()) {
            return game.getBoard().commitMove(dropMove);
        }
        throw new GameException("Invalid move.");
    }

    public boolean makeMove(String gameId, String playerId, Move move) {
        Game game = getGameByID(gameId);
//            if (game.getPlayer1() == null || game.getPlayer2() == null) {
//                throw new GameException("The game hasn't started yet.");
//            }
        boolean isSente;
        if (playerId.equals(game.getPlayer1())) {
            isSente = true;
        } else if (playerId.equals(game.getPlayer2())) {
            isSente = false;
        } else {
            throw new GameException("Client making move must be a player.");
        }
        BoardMove boardMove = move.buildMove(game.getBoard());
        if (isSente == boardMove.isSente()) {
            return game.getBoard().commitMove(boardMove);
        }
        throw new GameException("Invalid move.");
    }

    public Map<String, ArrayList<String>> getLegalMoves (String gameId, String sessionId) {
        Game game = getGameByID(gameId);
        if (game.getPlayer1().equals(sessionId)) {
            return game.legalMoves(true);
        } else if (game.getPlayer2().equals(sessionId)) {
            return game.legalMoves(false);
        }
        return null;
    }

    public Map<String, ArrayList<String>> getLegalDrops (String gameId, String sessionId) {
        Game game = getGameByID(gameId);
        if (game.getPlayer1().equals(sessionId)) {
            return game.legalDrops(true);
        } else if (game.getPlayer2().equals(sessionId)) {
            return game.legalDrops(false);
        }
        return null;
    }

    public Game getGameByID(String gameID) {
        Game game = gameRepository.findById(gameID);
        if (game == null) {
            throw new GameException("Game not found.");
        }
        return game;
    }
}
