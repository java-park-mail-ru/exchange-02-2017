package com.cyclic.services.game;

import com.cyclic.models.game.Player;
import org.springframework.web.socket.WebSocketSession;

import javax.validation.constraints.NotNull;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by serych on 03.04.17.
 */
public class PlayerManager {
    private ConcurrentHashMap<WebSocketSession, Player> players;

    public PlayerManager() {
        players = new ConcurrentHashMap<>();
    }

    public Player getPlayerForSession(WebSocketSession session) {
        return players.get(session);
    }

    public void createPlayer(WebSocketSession session, Player player) {
        players.put(session, player);
    }

    public void deletePlayerBySession(WebSocketSession session) {
        players.remove(session);
    }

    public void deletePlayer(Player player) {
        if (player == null)
            return;
        players.remove(player.getWebSocketSession());
    }
}
