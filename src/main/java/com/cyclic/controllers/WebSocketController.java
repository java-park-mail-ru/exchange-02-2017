package com.cyclic.controllers;

import com.cyclic.LOG;
import com.cyclic.models.WebSocketAnswer;
import com.cyclic.services.game.RoomManager;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;


public class WebSocketController extends TextWebSocketHandler {

    private RoomManager roomManager = new RoomManager();

    @Override
    public void handleTransportError(WebSocketSession session, Throwable throwable) throws Exception {
        System.out.println("error occured at sender " + session);
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {

    }

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {

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
            if (webSocketAnswer.getActionCode() == WebSocketAnswer.READY_FOR_GAME) {
                roomManager.findRoomForThisGuy(session);
            }
        }
    }


}