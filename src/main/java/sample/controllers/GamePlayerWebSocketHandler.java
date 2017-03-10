package sample.controllers;

import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;
import sample.services.ConnectedSessionsService;

@Component
public class GamePlayerWebSocketHandler extends TextWebSocketHandler {

    @Override
    public void handleTransportError(WebSocketSession session, Throwable throwable) throws Exception {
        //System.out.println("error occured at sender " + session);
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        ConnectedSessionsService.removeClient(session);
    }

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        ConnectedSessionsService.addClient(session);
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage jsonTextMessage) throws Exception {

    }
    
    
}
