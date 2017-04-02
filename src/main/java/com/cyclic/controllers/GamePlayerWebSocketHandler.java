package com.cyclic.controllers;

import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;
import com.cyclic.services.ConnectedSessionsService;


public class GamePlayerWebSocketHandler extends TextWebSocketHandler {

    private final ConnectedSessionsService connectedSessionsService;

    public GamePlayerWebSocketHandler(ConnectedSessionsService connectedSessionsService){
        this.connectedSessionsService = connectedSessionsService;
    }

    @Override
    public void handleTransportError(WebSocketSession session, Throwable throwable) throws Exception {
        System.out.println("error occured at sender " + session);
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        connectedSessionsService.removeClient(session);
    }

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        connectedSessionsService.addClient(session);
    }


    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage jsonTextMessage) throws Exception {

    }
    
    
}
