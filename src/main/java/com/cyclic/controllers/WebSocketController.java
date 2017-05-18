package com.cyclic.controllers;

import com.cyclic.LOG;
import com.cyclic.models.base.User;
import com.cyclic.models.game.Player;
import com.cyclic.models.game.net.fromclient.HelloMessage;
import com.cyclic.models.game.net.fromclient.WebSocketAnswer;
import com.cyclic.models.game.net.toclient.ConnectionError;
import com.cyclic.services.AccountServiceDB;
import com.cyclic.services.game.SessionManager;
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

import static com.cyclic.configs.Enums.Datatype.DATATYPE_PONG;
import static com.cyclic.configs.Enums.DisconnectReason.DISCONNECT_REASON_NOT_LOGINED;

@Service
public class WebSocketController extends TextWebSocketHandler {

    private RoomManager roomManager;
    private SessionManager sessionManager;
    private Gson gson;

    private AccountServiceDB accountService;

    @Autowired
    public WebSocketController(RoomManager roomManager, AccountServiceDB accountService) {
        this.roomManager = roomManager;
        this.accountService = accountService;
        sessionManager = new SessionManager();
        gson = new GsonBuilder().create();
    }

    @Override
    public void handleTransportError(WebSocketSession session, Throwable throwable) throws Exception {
        //System.out.println("errorConsole occured at sender " + session);
        //TreeSet
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        Player player = sessionManager.getPlayerForSession(session);
        if (player != null) {
            roomManager.deletePlayerFromAnyRoom(player, false);
            sessionManager.deletePlayer(player);
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
        sessionManager.createPlayer(session, player);
        LOG.webSocketLog("New websocket connected. IP: " + session.getRemoteAddress() +
                ", Nick: " + player.getNickname());
        session.sendMessage(new TextMessage(gson.toJson(new HelloMessage(player.getNickname(), player.getId()))));
        roomManager.addPlayerWithNoRoom(player);
    }


    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage jsonTextMessage) throws Exception {
        String message = jsonTextMessage.getPayload();
        WebSocketAnswer webSocketAnswer = null;
        try {
            webSocketAnswer = new Gson().fromJson(message, WebSocketAnswer.class);
        } catch (JsonSyntaxException e) {
            LOG.error(e);
        }
        if (webSocketAnswer != null) {
            Player player = sessionManager.getPlayerForSession(session);
            if (player == null)
                return;
            if (webSocketAnswer.getAction() == null)
                player.disconnectBadApi("Bad actionCode");
            LOG.webSocketLog("Message from " + player.getNickname() + " (Ip " + session.getRemoteAddress() + "). Code: " + webSocketAnswer.getAction());
            try {
                switch (webSocketAnswer.getAction()) {
                    case ACTION_READY_FOR_ROOM_SEARCH:
                        roomManager.findRoomForThisGuy(player, webSocketAnswer.getRoomCapacity());
                        break;
                    case ACTION_EXIT_ROOM:
                        roomManager.deletePlayerFromAnyRoom(player, true);
                        break;
                    case ACTION_PING:
                        player.sendDatatype(DATATYPE_PONG);
                        break;
                    case ACTION_GAME_MOVE:
                        if (player.getRoom() == null) {
                            player.disconnectBadApi("You move while not in the room");
                            break;
                        }
                        if (webSocketAnswer.getMove() == null) {
                            player.disconnectBadApi("You need to specify move!");
                            break;
                        }
                        player.getRoom().acceptMove(player, webSocketAnswer.getMove());
                        break;
                    default:
                        player.disconnectBadApi("Unknown actionCode");
                }
            }
            catch (Exception e) {
                LOG.errorConsole(e);
            }
        } else {
            session.close();
        }
    }


}