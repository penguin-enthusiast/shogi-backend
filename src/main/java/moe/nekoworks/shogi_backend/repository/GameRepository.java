package moe.nekoworks.shogi_backend.repository;

import moe.nekoworks.shogi_backend.model.Game;
import moe.nekoworks.shogi_backend.model.GameStatus;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.List;

@Repository
public class GameRepository {

    private static final HashMap<String, Game> games = new HashMap<>();

    public GameRepository() {
    }

    public void save(Game game) {
        games.put(game.getGameId(), game);
    }

    public Game findByGameId(String gameId) {
        return games.get(gameId);
    }

    public Game findByPlayerId(String playerId) {
        for (String gameId : games.keySet()) {
            Game game = games.get(gameId);
            if (playerId.equals(game.getPlayer1()) || playerId.equals(game.getPlayer2())) {
                return game;
            }
        }
        return null;
    }

    public Game findAvailableGame() {
        List<Game> availableGames = games.values().stream().filter(g -> g.getStatus() == GameStatus.WAITING).toList();
        if(availableGames.isEmpty()) {
            throw new RuntimeException();
        }
        return availableGames.getFirst();
    }

    public void deleteGame(String gameId) {
        games.remove(gameId);
    }
}
