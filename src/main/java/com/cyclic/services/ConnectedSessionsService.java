package com.cyclic.services;

import org.springframework.stereotype.Service;
import org.springframework.web.socket.WebSocketSession;

import java.util.Vector;

@SuppressWarnings({"unused", "DefaultFileTemplate"})
@Service
public class ConnectedSessionsService {
    private final Vector<WebSocketSession> webSocketSessions = new Vector<>();

    public void addClient(WebSocketSession session) {
        webSocketSessions.add(session);
    }

    public void removeClient(WebSocketSession session) {
        webSocketSessions.remove(session);
    }

    public Vector<WebSocketSession> getClients() {
        return webSocketSessions;
    }

}
