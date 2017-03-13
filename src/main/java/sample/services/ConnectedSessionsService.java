package sample.services;

import org.springframework.stereotype.Service;
import org.springframework.web.socket.WebSocketSession;

import java.util.Vector;

@SuppressWarnings({"unused", "DefaultFileTemplate"})
@Service
public class ConnectedSessionsService {
    static private Vector<WebSocketSession> webSocketSessions = new Vector<>();

    static public void addClient(WebSocketSession session) {
        webSocketSessions.add(session);
    }

    static public void removeClient(WebSocketSession session) {
        webSocketSessions.remove(session);
    }

    static public Vector<WebSocketSession> getClients() {
        return webSocketSessions;
    }

}
