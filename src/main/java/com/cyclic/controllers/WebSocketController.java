package com.cyclic.controllers;

import com.cyclic.LOG;
import com.cyclic.models.game.Player;
import com.cyclic.models.game.Room;
import com.cyclic.models.game.net.fromclient.WebSocketAnswer;
import com.cyclic.models.game.net.toclient.ConnectionError;
import com.cyclic.models.game.net.toclient.HelloMessage;
import com.cyclic.services.AccountServiceDB;
import com.cyclic.services.game.RoomManager;
import com.cyclic.services.game.SessionManager;
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

    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        Player player = sessionManager.getPlayerForSession(session);
        if (player != null) {
            Room room = player.getRoom();
            roomManager.removePlayerWithNoRoom(player);

            if (room != null) {
                room.removePlayer(player, false);
                room.removeSpectator(player);
            }
            sessionManager.deletePlayer(player);
            player.setRoom(null);
            LOG.webSocketLog("Websocket disconnected.  IP: " + session.getRemoteAddress() +
                    ", Nick: " + player.getNickname());
        }
    }

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        String nickname = (String) session.getAttributes().get("nickname");

        if (nickname == null) {
            LOG.errorConsole("Unlogined user try to play");
            session.sendMessage(new TextMessage(gson.toJson(new ConnectionError(DISCONNECT_REASON_NOT_LOGINED, "You are not authorized!"))));
            session.close();
            return;
        }
        if (sessionManager.nickIsInGame(nickname)) {
            LOG.errorConsole("User tryes to play from different PC");
            session.sendMessage(new TextMessage(gson.toJson(new ConnectionError(DISCONNECT_REASON_NOT_LOGINED, "You are already playing from other PC!"))));
            session.close();
            return;
        }
        Player player = new Player(session, nickname);
        sessionManager.createPlayer(session, player);
        LOG.webSocketLog("New websocket connected. IP: " + session.getRemoteAddress() +
                ", Nick: " + player.getNickname());
        session.sendMessage(new TextMessage(gson.toJson(new HelloMessage(player.getNickname()))));
        roomManager.addPlayerWithNoRoom(player);
    }


    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage jsonTextMessage) throws Exception {
        try {
            String message = jsonTextMessage.getPayload();
            WebSocketAnswer webSocketAnswer = null;
            try {
                webSocketAnswer = new Gson().fromJson(message, WebSocketAnswer.class);
            } catch (JsonSyntaxException e) {
                LOG.error(e);
            }
            if (webSocketAnswer != null) {
                Player player = sessionManager.getPlayerForSession(session);
                if (player == null) {
                    LOG.errorConsole("Cannot find player for session!!");
                    return;
                }
                if (webSocketAnswer.getAction() == null) {
                    player.disconnectBadApi("Bad actionCode");
                    return;
                }
                LOG.webSocketLog("Message from " + player.getNickname() + " (Ip " + session.getRemoteAddress() + "). Code: " + webSocketAnswer.getAction());
                try {
                    switch (webSocketAnswer.getAction()) {
                        case ACTION_PING:
                            player.sendDatatype(DATATYPE_PONG);
                            break;
                        case ACTION_GIVE_ME_ROOM:
                            if (player.getRoom() != null) {
                                player.disconnectBadApi("You cannot find new room while in another one");
                                break;
                            }
                            roomManager.findRoomForThisGuy(player, webSocketAnswer.getRoomCapacity());
                            break;
                        case ACTION_EXIT_ROOM:
                            Room room = player.getRoom();
                            if (room == null) {
                                player.disconnectBadApi("Cannot leave room if you do not have one!");
                                break;
                            }
                            room.removeSpectator(player);
                            room.removePlayer(player, false);
                            roomManager.addPlayerWithNoRoom(player);
                            break;

                        case ACTION_GAME_MOVE:
                            if (player.getRoom() == null) {
                                player.disconnectBadApi("You move while not in the room");
                                break;
                            }
                            if (player.getRoom().isSpectator(player)) {
                                player.disconnectBadApi("You move while you are spectator");
                                break;
                            }
                            if (webSocketAnswer.getMove() == null) {
                                player.disconnectBadApi("You need to specify move!");
                                break;
                            }
                            if (!webSocketAnswer.getMove().isValid()) {
                                player.disconnectBadApi("Your move is not valid!");
                                break;
                            }
                            player.getRoom().acceptMove(player, webSocketAnswer.getMove());
                            break;
                    }
                } catch (Exception e) {
                    LOG.errorConsole(e);
                }
            } else {
                session.close();
            }
        } catch (Exception e) {
            LOG.errorConsole(e);
        }
    }


}