package moe.nekoworks.shogi_backend.repository;

import moe.nekoworks.shogi_backend.model.Game;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

@Repository
public class GameRepository {

    private static final HashMap<String, Game> games = new HashMap<>();

    public GameRepository() {
    }

    public void save(Game game) {
        games.put(game.getGameId(), game);
    }

    public Game findById(String gameId) {
        return games.get(gameId);
    }

    public Game findAvailableGame() {
        List<Game> availableGames = games.values().stream().filter(g -> g.getPlayer2() == null).toList();
        if(availableGames.isEmpty()) {
            throw new RuntimeException();
        }
        return availableGames.getFirst();
    }
}
