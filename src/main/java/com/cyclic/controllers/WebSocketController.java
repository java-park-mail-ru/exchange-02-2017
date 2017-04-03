package com.cyclic.controllers;

import com.cyclic.LOG;
import com.cyclic.models.WebSocketAnswer;
import com.cyclic.models.game.Player;
import com.cyclic.services.game.PlayerManager;
import com.cyclic.services.game.RoomManager;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;


public class WebSocketController extends TextWebSocketHandler {

    private RoomManager roomManager = new RoomManager();
    private PlayerManager playerManager = new PlayerManager();

    @Override
    public void handleTransportError(WebSocketSession session, Throwable throwable) throws Exception {
        System.out.println("errorConsole occured at sender " + session);
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        Player player = playerManager.getPlayerForSession(session);
        roomManager.deletePlayerFromAnyRoom(player);
        playerManager.deletePlayer(session);
    }

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        playerManager.createPlayer(session);
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
            if (webSocketAnswer.getActionCode() == null)
                player.disconnectBadApi();
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
                    player.disconnectBadApi();
            }
        }
        else {
            session.close();
        }
    }


}