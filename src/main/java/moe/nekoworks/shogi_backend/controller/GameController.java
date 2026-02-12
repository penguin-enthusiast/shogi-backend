package moe.nekoworks.shogi_backend.controller;

import moe.nekoworks.shogi_backend.exception.GameException;
import moe.nekoworks.shogi_backend.exception.MoveException;
import moe.nekoworks.shogi_backend.model.*;
import moe.nekoworks.shogi_backend.service.GameService;
import moe.nekoworks.shogi_backend.shogi.move.AbstractMove;
import moe.nekoworks.shogi_backend.shogi.move.BoardMove;
import moe.nekoworks.shogi_backend.shogi.move.DropMove;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.MessageMapping;
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

    private final SimpMessagingTemplate simpMessagingTemplate;
    private final GameService gameService;

    public GameController(GameService gameService, SimpMessagingTemplate simpMessagingTemplate) {
        this.gameService = gameService;
        this.simpMessagingTemplate = simpMessagingTemplate;
    }

    @MessageMapping("/create")
    @SendToUser("/topic/game")
    public void create(@Header("simpSessionId") String sessionId) {
        Game game = gameService.createGame(sessionId);
        joinGame(sessionId, game);
    }

    @MessageMapping("/create-engine")
    @SendToUser("/topic/game")
    public void createEngineGame(@Header("simpSessionId") String sessionId, @RequestBody CreateEngineGameRequest request) {
        EngineGame game = gameService.createEngineGame(sessionId, request.getEngine(), this);
        joinGame(sessionId, game);
    }

    @MessageMapping("/join")
    public void joinById(@Header("simpSessionId") String sessionId, @RequestBody JoinRequest request) {
        try {
            Game game = gameService.joinGame(sessionId, request.getGameId());
            joinGame(sessionId, game);
            sendGameUpdate(game, null);
        } catch (RuntimeException e) {
            sendGameNotFoundResponse(sessionId);
        }
    }

    @MessageMapping("/join/random")
    public void joinRandom(@Header("simpSessionId") String sessionId) {
        try {
            Game game = gameService.connectToRandomGame(sessionId);
            joinGame(sessionId, game);
            sendGameUpdate(game, null);
        } catch (RuntimeException e) {
            sendGameNotFoundResponse(sessionId);
        }
    }

    public void joinGame(String sessionId, Game game) {
        String destination = "/topic/game";
        HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.set("playerId", sessionId);
        ResponseEntity<Game> response = ResponseEntity.ok()
                .headers(responseHeaders)
                .body(game);
        SimpMessageHeaderAccessor headerAccessor = SimpMessageHeaderAccessor.create(SimpMessageType.MESSAGE);
        headerAccessor.setSessionId(sessionId);
        headerAccessor.setLeaveMutable(true);
        simpMessagingTemplate.convertAndSendToUser(sessionId, destination, response, headerAccessor.getMessageHeaders());
    }

    @MessageMapping("/game/{gameId}/setReadyStatus")
    public void setReadyStatus(@DestinationVariable String gameId, @Header("simpSessionId") String sessionId) {
        Game game = gameService.togglePlayerReadyStatus(gameId, sessionId);
        if (game.getStatus() == GameStatus.IN_PROGRESS) {
            sendLegalMoves(game.getGameId());
        }
        sendGameUpdate(game, null);
    }

    public void sendGameUpdate(Game game, HttpHeaders responseHeaders) {
        String destination = "/topic/game/" + game.getGameId();
        ResponseEntity<Game> response = ResponseEntity.ok()
                .headers(responseHeaders)
                .body(game);

        SimpMessageHeaderAccessor headerAccessor = SimpMessageHeaderAccessor.create(SimpMessageType.MESSAGE);
        headerAccessor.setLeaveMutable(true);
        simpMessagingTemplate.convertAndSend(destination, response, headerAccessor.getMessageHeaders());
    }

    @MessageMapping("/game/{gameId}/drop")
    public void makeDrop(@DestinationVariable String gameId,
                         @Header("simpSessionId") String sessionId,
                         @RequestBody Drop drop) {
        makeAbstractMove(gameId, sessionId, drop);
    }

    @MessageMapping("/game/{gameId}/move")
    public void makeMove(@DestinationVariable String gameId,
                         @Header("simpSessionId") String sessionId,
                         @RequestBody Move move) {
        makeAbstractMove(gameId, sessionId, move);
        }

    public void makeAbstractMove(String gameId, String sessionId, AbstractSGBoardAction<?> action) {
        String destination = "/topic/game/" + gameId + "/move";
        try {
            Game game = getGame(gameId);
            try {
                AbstractMove move = gameService.makeMove(game, sessionId, action);
                action.setMoveString(move.toString());
            } catch (MoveException e) {
                sendGameUpdate(game, null);
            } finally {
                postMoveLogic(game, action);
            }
        } catch (GameException e) {
            // TODO do something with the exception
            simpMessagingTemplate.convertAndSend(destination, ResponseEntity.badRequest().build());
        }
    }

    public void postMoveLogic(Game game, AbstractSGBoardAction<?> action) {
        sendMove(game.getGameId(), action);
        if (action.getClass() == Move.class) {
            Move move = (Move) action;
            if (move.getCapturedPiece() != null && move.getCapturedPiece().getRole().equals("king")) {
                sendGameOverMessage(game.getGameId());
                return;
            }
        }
        sendLegalMoves(game.getGameId());
    }

    public void sendLegalMoves(String gameId) {
        Game game = getGame(gameId);
        String destination = "/topic/game/" + gameId + "/legalMoves";
        for (String s : gameService.getConnectedClients(game)) {
            Map<String, ArrayList<String>> movesP1 = gameService.getLegalMoves(gameId, s);
            ResponseEntity<Map<String, ArrayList<String>>> responseP1 = ResponseEntity.ok(movesP1);

            SimpMessageHeaderAccessor headerAccessor = SimpMessageHeaderAccessor.create(SimpMessageType.MESSAGE);
            headerAccessor.setSessionId(s);
            headerAccessor.setLeaveMutable(true);

            simpMessagingTemplate.convertAndSendToUser(s, destination, responseP1, headerAccessor.getMessageHeaders());
        }
    }

    @MessageMapping("/game/{gameId}/disconnect")
    public void disconnectRequest(@DestinationVariable String gameId, @Header("simpSessionId") String sessionId) {
        try {
            playerDisconnected(sessionId, getGame(gameId));
        } catch (GameException e) {
            sendGameNotFoundResponse(sessionId);
        }
    }

    public Game getGame(String gameId) {
        return gameService.getGameById(gameId);
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
        sendGameUpdate(game, responseHeaders);
    }

    public void sendMove(String gameId, AbstractSGBoardAction<?> action) {
        String destination = "/topic/game/" + gameId + "/move";
        HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.set("moveType", action.type());
        ResponseEntity<AbstractSGBoardAction<?>> response = ResponseEntity.ok()
                .headers(responseHeaders)
                .body(action);

        SimpMessageHeaderAccessor headerAccessor = SimpMessageHeaderAccessor.create(SimpMessageType.MESSAGE);
        headerAccessor.setLeaveMutable(true);
        simpMessagingTemplate.convertAndSend(destination, response, headerAccessor.getMessageHeaders());
    }

    public void sendGameOverMessage(String gameId) {
        Game game = gameService.getGameById(gameId);
        AbstractMove lastMove = game.getBoard().getLastMove();

        String winner = lastMove.isSente() ? game.getPlayer1() : game.getPlayer2();
        String loser = lastMove.isSente() ? game.getPlayer2() : game.getPlayer1();

        HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.set("winner", winner);
        responseHeaders.set("loser", loser);
        responseHeaders.set("method", "normal");

        gameService.finishGame(game);
        sendGameUpdate(game, responseHeaders);
    }

    public void sendGameNotFoundResponse(String SessionId) {
        String destination = "/topic/game";

        ResponseEntity<Object> responseP1 = ResponseEntity.notFound().build();
        SimpMessageHeaderAccessor headerAccessor = SimpMessageHeaderAccessor.create(SimpMessageType.MESSAGE);
        headerAccessor.setSessionId(SessionId);
        headerAccessor.setLeaveMutable(true);

        simpMessagingTemplate.convertAndSendToUser(SessionId, destination, responseP1, headerAccessor.getMessageHeaders());
    }
}
