package moe.nekoworks.shogi_backend.controller;

import moe.nekoworks.shogi_backend.exception.GameException;
import moe.nekoworks.shogi_backend.model.Drop;
import moe.nekoworks.shogi_backend.model.JoinRequest;
import moe.nekoworks.shogi_backend.model.Game;
import moe.nekoworks.shogi_backend.model.Move;
import moe.nekoworks.shogi_backend.service.GameService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessageType;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.annotation.SendToUser;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.Map;

@RestController
@RequestMapping("/game")
public class GameController {

    @Autowired
    SimpMessagingTemplate simpMessagingTemplate;
    private final GameService gameService;

    public GameController(GameService gameService) {
        this.gameService = gameService;
    }

    @MessageMapping("/create")
    @SendToUser("/topic/game")
    public ResponseEntity<Game> create(@Header("simpSessionId") String sessionId) {
        return ResponseEntity.ok(gameService.createGame(sessionId));
    }

    @MessageMapping("/join")
    @SendToUser("/topic/game")
    public ResponseEntity<Game> join(@Header("simpSessionId") String sessionId, @RequestBody JoinRequest request) {
        try {
            return ResponseEntity.ok(gameService.joinGame(sessionId, request.getGameId()));
        } catch (RuntimeException e) {
            System.out.println("game not found");
            return ResponseEntity.notFound().build();
        }
    }

    @MessageMapping("/join/random")
    @SendToUser("/topic/game")
    public ResponseEntity<Game> joinRandom(@Header("simpSessionId") String sessionId) {
        try {
            return ResponseEntity.ok(gameService.connectToRandomGame(sessionId));
        } catch (RuntimeException e) {
            System.out.println("no game available");
            return ResponseEntity.notFound().build();
        }
    }

    @MessageMapping("/game/{gameId}/drop")
    @SendTo("/topic/game/{gameId}/move")
    public ResponseEntity<Drop> makeDrop(@DestinationVariable String gameId,
                                         @Header("simpSessionId") String sessionId,
                                         @RequestBody Drop drop) {
        System.out.println("GameController.makeDrop    gameId: " + gameId + "    session: " + sessionId);
        try {
            boolean moveSuccess = gameService.makeDrop(gameId, sessionId, drop);
            if (moveSuccess) {
                sendLegalMoves(gameId);
                HttpHeaders responseHeaders = new HttpHeaders();
                responseHeaders.set("moveType", "drop");
                return ResponseEntity.ok()
                        .headers(responseHeaders)
                        .body(drop);
            }
            return ResponseEntity.badRequest().build();
        } catch (GameException e) {
            // TODO do something with the exception
            return ResponseEntity.badRequest().build();
        }
    }

    @MessageMapping("/game/{gameId}/move")
    @SendTo("/topic/game/{gameId}/move")
    public ResponseEntity<Move> makeMove(@DestinationVariable String gameId,
                                            @Header("simpSessionId") String sessionId,
                                            @RequestBody Move move) {
        System.out.println("GameController.makeMove    gameId: " + gameId + "    session: " + sessionId);
        try {
            boolean moveSuccess = gameService.makeMove(gameId, sessionId, move);
            if (moveSuccess) {
                sendLegalMoves(gameId);
                HttpHeaders responseHeaders = new HttpHeaders();
                responseHeaders.set("moveType", "move");
                return ResponseEntity.ok()
                        .headers(responseHeaders)
                        .body(move);
            }
            return ResponseEntity.badRequest().build();
        } catch (GameException e) {
            // TODO do something with the exception
            return ResponseEntity.badRequest().build();
        }
    }

    public void sendLegalMoves(String gameId) {
        Game game = gameService.getGameByID(gameId);
        String destination = "/topic/game/" + gameId + "/legalMoves";
        String player1 = game.getPlayer1();
        String player2 = game.getPlayer2();
        Map<String, ArrayList<String>> movesP1 = gameService.getLegalMoves(gameId, player1);
        Map<String, ArrayList<String>> movesP2 = gameService.getLegalMoves(gameId, player2);
        ResponseEntity<Map<String, ArrayList<String>>> responseP1 = ResponseEntity.ok(movesP1);
        ResponseEntity<Map<String, ArrayList<String>>> responseP2 = ResponseEntity.ok(movesP2);

        SimpMessageHeaderAccessor headerAccessorP1 = SimpMessageHeaderAccessor.create(SimpMessageType.MESSAGE);
        headerAccessorP1.setSessionId(player1);
        headerAccessorP1.setLeaveMutable(true);
        SimpMessageHeaderAccessor headerAccessorP2 = SimpMessageHeaderAccessor.create(SimpMessageType.MESSAGE);
        headerAccessorP2.setSessionId(player2);
        headerAccessorP2.setLeaveMutable(true);
        simpMessagingTemplate.convertAndSendToUser(player1, destination, responseP1, headerAccessorP1.getMessageHeaders());
        simpMessagingTemplate.convertAndSendToUser(player2, destination, responseP2, headerAccessorP2.getMessageHeaders());
    }

    @MessageMapping("/game/{gameId}/getMoves")
    @SendToUser("/topic/game/{gameId}/legalMoves")
    public ResponseEntity<Map<String, ArrayList<String>>> getLegalMoves(@DestinationVariable String gameId,
                                                                  @Header("simpSessionId") String sessionId) {
        return ResponseEntity.ok(gameService.getLegalMoves(gameId, sessionId));
    }

    @MessageMapping("/game/{gameId}/getDrops")
    @SendToUser("/topic/game/{gameId}/legalDrops")
    public ResponseEntity<Map<String, ArrayList<String>>> getLegalDrops(@DestinationVariable String gameId,
                                              @Header("simpSessionId") String sessionId) {
        return ResponseEntity.ok(gameService.getLegalDrops(gameId, sessionId));

    }
}
