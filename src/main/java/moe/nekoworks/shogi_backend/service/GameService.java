package moe.nekoworks.shogi_backend.service;

import moe.nekoworks.shogi_backend.exception.GameException;
import moe.nekoworks.shogi_backend.misc.Utils;
import moe.nekoworks.shogi_backend.model.Drop;
import moe.nekoworks.shogi_backend.model.Game;
import moe.nekoworks.shogi_backend.model.Move;
import moe.nekoworks.shogi_backend.repository.GameRepository;
import moe.nekoworks.shogi_backend.shogi.move.BoardMove;
import moe.nekoworks.shogi_backend.shogi.move.DropMove;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

@Service
public class GameService {

    private final GameRepository gameRepository;

    public GameService(GameRepository gameRepository) {
        this.gameRepository = gameRepository;
    }

    public Game createGame(String playerId) {
        Game game = new Game(playerId);
        gameRepository.save(game);
        return game;
    }

    public Game joinGame(String playerId, String gameID) {
        Game game = getGameById(gameID);
        if (game.getPlayer2() != null) {
            throw new GameException("Someone already joined this game.");
        }
        game.joinGame(playerId);
        return game;
    }

    public Game connectToRandomGame(String playerId) {
        Game game = gameRepository.findAvailableGame();
        game.joinGame(playerId);
        return game;
    }

    public Game togglePlayerReadyStatus(String gameId, String playerId) {
        Game game = getGameById(gameId);
        if (playerId.equals(game.getPlayer1())) {
            game.setPlayer1Ready(!game.isPlayer1Ready());
        } else if (playerId.equals(game.getPlayer2())) {
            game.setPlayer2Ready(!game.isPlayer2Ready());
        } else {
            throw new GameException("Not a player");
        }
        if (game.isPlayer1Ready() && game.isPlayer2Ready()) {
            game.startGame();
        }
        return game;
    }

    public boolean makeDrop(String gameId, String playerId, Drop drop) {
        Game game = getGameById(gameId);
        if (game.getPlayer1() == null || game.getPlayer2() == null) {
            throw new GameException("The game hasn't started yet.");
        }
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
        Game game = getGameById(gameId);
        if (Utils.StringIsEmpty(game.getPlayer1()) || Utils.StringIsEmpty(game.getPlayer2())) {
            throw new GameException("The game hasn't started yet.");
        }
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
        Game game = getGameById(gameId);
        if (game.getPlayer1().equals(sessionId)) {
            return game.legalMoves(true);
        } else if (game.getPlayer2().equals(sessionId)) {
            return game.legalMoves(false);
        }
        return null;
    }

    public Map<String, ArrayList<String>> getLegalDrops (String gameId, String sessionId) {
        Game game = getGameById(gameId);
        if (game.getPlayer1().equals(sessionId)) {
            return game.legalDrops(true);
        } else if (game.getPlayer2().equals(sessionId)) {
            return game.legalDrops(false);
        }
        return null;
    }

    public Game getGameById(String gameID) {
        Game game = gameRepository.findByGameId(gameID);
        if (game == null) {
            throw new GameException("Game not found.");
        }
        return game;
    }

    public Game getGameByPlayerId(String playerId) {
        return gameRepository.findByPlayerId(playerId);
    }

    public void finishGame(Game game) {
        game.finishGame();
        // delete game from memory for now, maybe save to db in future?
        gameRepository.deleteGame(game.getGameId());
    }

    public Set<String> getConnectedClients(Game game) {
        HashSet<String> clients = new HashSet<>();
        clients.add(game.getPlayer1());
        clients.add(game.getPlayer2());
        return clients;
    }
}
