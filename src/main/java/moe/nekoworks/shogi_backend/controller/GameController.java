package moe.nekoworks.shogi_backend.controller;

import moe.nekoworks.shogi_backend.exception.GameException;
import moe.nekoworks.shogi_backend.model.Drop;
import moe.nekoworks.shogi_backend.model.JoinRequest;
import moe.nekoworks.shogi_backend.model.Game;
import moe.nekoworks.shogi_backend.model.Move;
import moe.nekoworks.shogi_backend.service.GameService;
import moe.nekoworks.shogi_backend.shogi.move.AbstractMove;
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
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

@RestController
@RequestMapping("/game")
public class GameController {

    private final SimpMessagingTemplate simpMessagingTemplate;
    private final GameService gameService;

    public GameController(GameService gameService, SimpMessagingTemplate simpMessagingTemplate) {
        this.gameService = gameService;
        this.simpMessagingTemplate = simpMessagingTemplate;
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
            Game game = gameService.joinGame(sessionId, request.getGameId());
            sendGameUpdate(game, game.getPlayer1(), null);
            sendLegalMoves(game.getGameId());
            return ResponseEntity.ok(game);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @MessageMapping("/join/random")
    @SendToUser("/topic/game")
    public ResponseEntity<Game> joinRandom(@Header("simpSessionId") String sessionId) {
        try {
            Game game = gameService.connectToRandomGame(sessionId);
            sendGameUpdate(game, game.getPlayer1(), null);
            sendLegalMoves(game.getGameId());
            return ResponseEntity.ok(game);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    public void sendGameUpdate(Game game, String client, HttpHeaders responseHeaders) {
        HashSet<String> clients = new HashSet<>();
        clients.add(client);
        sendGameUpdate(game, clients, responseHeaders);
    }

    public void sendGameUpdate(Game game, Set<String> clients, HttpHeaders responseHeaders) {
        String destination = "/topic/game/" + game.getGameId();
        ResponseEntity<Game> response = ResponseEntity.ok()
                .headers(responseHeaders)
                .body(game);

        for (String s : clients) {
            SimpMessageHeaderAccessor headerAccessor = SimpMessageHeaderAccessor.create(SimpMessageType.MESSAGE);
            headerAccessor.setSessionId(s);
            headerAccessor.setLeaveMutable(true);
            simpMessagingTemplate.convertAndSend(destination, response, headerAccessor.getMessageHeaders());
        }
    }

    @MessageMapping("/game/{gameId}/drop")
    @SendTo("/topic/game/{gameId}/move")
    public ResponseEntity<Drop> makeDrop(@DestinationVariable String gameId,
                                         @Header("simpSessionId") String sessionId,
                                         @RequestBody Drop drop) {
        System.out.println("GameController.makeDrop    gameId: " + gameId + "    session: " + sessionId);
        try {
            gameService.makeDrop(gameId, sessionId, drop);
            sendLegalMoves(gameId);
            HttpHeaders responseHeaders = new HttpHeaders();
            responseHeaders.set("moveType", "drop");
            return ResponseEntity.ok()
                    .headers(responseHeaders)
                    .body(drop);
        } catch (GameException e) {
            // TODO do something with the exception
            return ResponseEntity.badRequest().build();
        }
    }

    @MessageMapping("/game/{gameId}/move")
    public void makeMove(@DestinationVariable String gameId,
                                            @Header("simpSessionId") String sessionId,
                                            @RequestBody Move move) {
        System.out.println("GameController.makeMove    gameId: " + gameId + "    session: " + sessionId);
        String destination = "/topic/game/" + gameId + "/move";
        try {
            boolean kingCapture = gameService.makeMove(gameId, sessionId, move);
            sendLegalMoves(gameId);

            HttpHeaders responseHeaders = new HttpHeaders();
            responseHeaders.set("moveType", "move");
            ResponseEntity<Move> response = ResponseEntity.ok()
                    .headers(responseHeaders)
                    .body(move);

            SimpMessageHeaderAccessor headerAccessor = SimpMessageHeaderAccessor.create(SimpMessageType.MESSAGE);
            headerAccessor.setLeaveMutable(true);
            simpMessagingTemplate.convertAndSend(destination, response, headerAccessor.getMessageHeaders());

            if (kingCapture) {
                sendGameOverMessage(gameId);
            }
        } catch (GameException e) {
            // TODO do something with the exception
            simpMessagingTemplate.convertAndSend(destination, ResponseEntity.badRequest().build());
        }
    }

    public void sendLegalMoves(String gameId) {
        Game game = gameService.getGameById(gameId);
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
        try {
            return ResponseEntity.ok(gameService.getLegalMoves(gameId, sessionId));
        } catch (GameException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @MessageMapping("/game/{gameId}/getDrops")
    @SendToUser("/topic/game/{gameId}/legalDrops")
    public ResponseEntity<Map<String, ArrayList<String>>> getLegalDrops(@DestinationVariable String gameId,
                                                                        @Header("simpSessionId") String sessionId) {
        try {
            return ResponseEntity.ok(gameService.getLegalDrops(gameId, sessionId));
        } catch (GameException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @MessageMapping("/game/{gameId}/disconnect")
    public void disconnectRequest (@DestinationVariable String gameId, @Header("simpSessionId") String sessionId) {
        try {
            playerDisconnected(sessionId, gameService.getGameById(gameId));
        } catch (GameException e) {
            System.out.println("Couldn't find game.");
        }
    }

    public void playerDisconnected(String playerId) {
        Game game = gameService.getGameByPlayerId(playerId);
        if (game == null) {
            return;
        }
        playerDisconnected(playerId, game);
    }

    public void playerDisconnected(String playerId, Game game) {
        final String otherPlayer;
        if (playerId.equals(game.getPlayer1())) {
            otherPlayer = game.getPlayer2();
        } else {
            otherPlayer = game.getPlayer1();
        }
        HttpHeaders responseHeaders = new HttpHeaders();
        if (game.getPlayer2() != null) {
            responseHeaders.set("winner", otherPlayer);
            responseHeaders.set("loser", playerId);
        }
        responseHeaders.set("method", "disconnect");

        gameService.finishGame(game);
        System.out.println("disconnected from game, status is now " + game.getStatus());
        sendGameUpdate(game, otherPlayer, responseHeaders);
    }

    public void sendGameOverMessage(String gameId) {
        Game game = gameService.getGameById(gameId);
        AbstractMove lastMove = game.getBoard().getLastMove();

        String winner = lastMove.isSente() ? game.getPlayer1() : game.getPlayer2();
        String loser = lastMove.isSente() ? game.getPlayer2() : game.getPlayer1();

        Set<String> clients = gameService.getConnectedClients(game);
        HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.set("winner", winner);
        responseHeaders.set("loser", loser);
        responseHeaders.set("method", "normal");

        gameService.finishGame(game);
        sendGameUpdate(game, clients, responseHeaders);
    }
}
