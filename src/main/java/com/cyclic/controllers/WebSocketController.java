package com.cyclic.controllers;

import com.cyclic.LOG;
import com.cyclic.models.base.User;
import com.cyclic.models.game.net.fromclient.HelloMessage;
import com.cyclic.models.game.net.fromclient.WebSocketAnswer;
import com.cyclic.models.game.Player;
import com.cyclic.models.game.net.toclient.ConnectionError;
import com.cyclic.services.AccountServiceDB;
import com.cyclic.services.game.PlayerManager;
import com.cyclic.services.game.RoomManager;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import static com.cyclic.configs.Enums.DisconnectReason.DISCONNECT_REASON_NOT_LOGINED;

@Service
public class WebSocketController extends TextWebSocketHandler {

    private RoomManager roomManager;
    private PlayerManager playerManager;
    private Gson gson;

    private AccountServiceDB accountService;

    @Autowired
    public WebSocketController(RoomManager roomManager, AccountServiceDB accountService) {
        this.roomManager = roomManager;
        this.accountService = accountService;
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
        final Long id = (Long) session.getAttributes().get("userId");
        User user = accountService.getUserById(id);
        if (user == null) {
            LOG.errorConsole("Unlogined user try to play");
            session.sendMessage(new TextMessage(gson.toJson(new ConnectionError(DISCONNECT_REASON_NOT_LOGINED, "You are not authorized!"))));
            session.close();
            return;
        }
        Player player = new Player(session, user.getLogin(), user.getId());
        playerManager.createPlayer(session, player);
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
            if (webSocketAnswer.getAction() == null)
                player.disconnectBadApi("Bad actionCode");
            LOG.webSocketLog("Message from " + player.getNickname()+ " (Ip " + session.getRemoteAddress() + "). Code: " + webSocketAnswer.getAction());
            switch (webSocketAnswer.getAction()) {
                case ACTION_READY_FOR_ROOM_SEARCH:
                    roomManager.findRoomForThisGuy(player);
                    break;
                case ACTION_READY_FOR_GAME_START:
                    player.setReadyForGameStart(true);
                    break;
                case ACTION_GAME_MOVE:
                    if (player.getRoom() == null)
                        player.disconnectBadApi("You move while not in the room");
                    player.getRoom().acceptMove(player, webSocketAnswer.getMove());
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