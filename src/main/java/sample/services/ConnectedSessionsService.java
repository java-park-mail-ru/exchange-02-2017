package sample.services;

import org.springframework.stereotype.Service;
import org.springframework.web.socket.WebSocketSession;

import java.util.Vector;

@SuppressWarnings({"unused", "DefaultFileTemplate"})
@Service
public class ConnectedSessionsService {
    private static Vector<WebSocketSession> webSocketSessions = new Vector<>();

    public static void addClient(WebSocketSession session) {
        webSocketSessions.add(session);
    }

    public static void removeClient(WebSocketSession session) {
        webSocketSessions.remove(session);
    }

    public static Vector<WebSocketSession> getClients() {
        return webSocketSessions;
    }

}
