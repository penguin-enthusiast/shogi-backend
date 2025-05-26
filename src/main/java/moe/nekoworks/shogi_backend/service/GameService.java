package moe.nekoworks.shogi_backend.service;

import moe.nekoworks.shogi_backend.model.Game;
import moe.nekoworks.shogi_backend.repository.GameRepository;
import moe.nekoworks.shogi_backend.shogi.move.Move;
import org.springframework.stereotype.Service;

import java.util.Objects;
import java.util.Optional;

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

    public Game connectToGame(String playerId, String gameID) {
        Game game = getGameByID(gameID);
        if (game.getPlayer2() != null) {
            throw new RuntimeException();
        }
        game.setPlayer2(playerId);
        return game;
    }

    public Game connectToRandomGame(String playerId) {
        Game game = gameRepository.findAvailableGame();
        game.setPlayer2(playerId);
        return game;
    }

    public boolean makeMove(String playerId, Move move, String gameID) {
        Game game = getGameByID(gameID);
        if ((move.isSente() && Objects.equals(game.getPlayer1(), playerId)) ||
                (!move.isSente() && Objects.equals(game.getPlayer2(), playerId))) {
            return game.getBoard().commitMove(move);
        }
        return false;
    }

    private Game getGameByID(String gameID) {
        Game game = gameRepository.findById(gameID);
        if (game == null) {
            throw new RuntimeException();
        }
        return game;
    }
}
