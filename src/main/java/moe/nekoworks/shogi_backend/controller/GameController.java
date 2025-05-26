package moe.nekoworks.shogi_backend.controller;


import moe.nekoworks.shogi_backend.model.ConnectionRequest;
import moe.nekoworks.shogi_backend.model.Game;
import moe.nekoworks.shogi_backend.service.GameService;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.annotation.SendToUser;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/game")
public class GameController {

    private final GameService gameService;

    public GameController(GameService gameService) {
        this.gameService = gameService;
    }

    @MessageMapping("/create")
    @SendToUser("/topic/game")
    public ResponseEntity<Game> create(@Header("simpSessionId") String sessionId) {
        return ResponseEntity.ok(gameService.createGame(sessionId));
    }

    @MessageMapping("/connect")
    @SendToUser("/topic/game")
    public ResponseEntity<Game> connect(@Header("simpSessionId") String sessionId, @RequestBody ConnectionRequest request) {
        try {
            return ResponseEntity.ok(gameService.connectToGame(sessionId, request.getGameId()));
        } catch (RuntimeException e) {
            System.out.println("game not found");
            return ResponseEntity.notFound().build();
        }
    }

    @MessageMapping("/connect/random")
    @SendToUser("/topic/game")
    public ResponseEntity<Game> connectRandom(@Header("simpSessionId") String sessionId) {
        try {
            return ResponseEntity.ok(gameService.connectToRandomGame(sessionId));
        } catch (RuntimeException e) {
            System.out.println("no game available");
            return ResponseEntity.notFound().build();
        }
    }
}
