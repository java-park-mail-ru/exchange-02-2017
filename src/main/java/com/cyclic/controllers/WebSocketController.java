package com.cyclic.controllers;

import com.cyclic.LOG;
import com.cyclic.models.User;
import com.cyclic.models.WebSocketAnswer;
import com.cyclic.models.game.Player;
import com.cyclic.models.game.net.ConnectionError;
import com.cyclic.models.game.net.HelloMessage;
import com.cyclic.services.AccountServiceDB;
import com.cyclic.services.game.PlayerManager;
import com.cyclic.services.game.RoomManager;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.util.concurrent.ThreadLocalRandom;


public class WebSocketController extends TextWebSocketHandler {

    public static final int DATATYPE_ROOMINFO = 1;
    public static final int DATATYPE_PLAYERMOVE = 2;
    public static final int DATATYPE_NEWBONUS = 3;
    public static final int DATATYPE_ERROR = 4;
    public static final int DATATYPE_HELLO = 5;
    public static final int DATATYPE_ROOM_DESTRUCTION = 6;
    public static final int DATATYPE_PLAYER_DISCONNECT = 7;
    public static final int DATATYPE_ACCEPT_MOVE = 8;

    private RoomManager roomManager;
    private PlayerManager playerManager;
    private Gson gson;

    private AccountServiceDB accountService;

    @Autowired
    public WebSocketController(AccountServiceDB accountService) {
        this.accountService = accountService;
        roomManager = new RoomManager();
        playerManager = new PlayerManager();
        gson = new GsonBuilder().create();
    }

    @Override
    public void handleTransportError(WebSocketSession session, Throwable throwable) throws Exception {
        //System.out.println("errorConsole occured at sender " + session);
        //TreeSet
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        Player player = playerManager.getPlayerForSession(session);
        if (player != null) {
            roomManager.deletePlayerFromAnyRoom(player);
            playerManager.deletePlayer(player);
            LOG.webSocketLog("Websocket disconnected.  IP: " + session.getRemoteAddress() +
                    ", Nick: " + player.getNickname());
        }
    }

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        //TODO: uncomment this, test and add to production when frontend will be ready
        final Long id = (Long) session.getAttributes().get("userId");
        User user = accountService.getUserById(id);
        if (user == null) {
            session.sendMessage(new TextMessage(gson.toJson(new ConnectionError(ConnectionError.DISCONNECT_REASON_NOT_LOGINED, ""))));
            session.close();
            return;
        }
        System.out.println(user.getLogin());
        Player player = new Player(session, user.getLogin(), user.getId());
        playerManager.createPlayer(session, player);
//        Player player = new Player(session,
//                "Nick" + ThreadLocalRandom.current().nextInt(0, 9999),
//                ThreadLocalRandom.current().nextInt(0, 9999));
//        playerManager.createPlayer(session, player);
        LOG.webSocketLog("New websocket connected. IP: " + session.getRemoteAddress() +
        ", Nick: " + player.getNickname());
        session.sendMessage(new TextMessage(gson.toJson(new HelloMessage(player.getNickname(), player.getId()))));
    }


    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage jsonTextMessage) throws Exception {
        String message = jsonTextMessage.getPayload();
        WebSocketAnswer webSocketAnswer = null;
        try {
             webSocketAnswer = new Gson().fromJson( message, WebSocketAnswer.class);
        }
        catch (JsonSyntaxException e) {
            LOG.error(e);
        }
        if (webSocketAnswer != null) {
            Player player = playerManager.getPlayerForSession(session);
            if (player == null)
                return;
            if (webSocketAnswer.getActionCode() == null)
                player.disconnectBadApi("There is no actionCode!");
            LOG.webSocketLog("Message from Ip " + session.getRemoteAddress() + ". Code: " + webSocketAnswer.getActionCode());
            switch (webSocketAnswer.getActionCode()) {
                case WebSocketAnswer.READY_FOR_ROOM_SEARCH:
                    roomManager.findRoomForThisGuy(player);
                    break;
                case WebSocketAnswer.READY_FOR_GAME_START:
                    player.setReadyForGameStart(true);
                    break;
                case WebSocketAnswer.GAME_UPDATE_MY_MOVE:
                    player.getRoom().handlePlayersMove(player, webSocketAnswer.getMoves());
                    break;
                case WebSocketAnswer.GAME_ACCEPT_MY_MOVE:
                    player.getRoom().acceptMove(player);
                    break;
                default:
                    player.disconnectBadApi("Unknown actionCode");
            }
        }
        else {
            session.close();
        }
    }


}