package com.cyclic.services.game;

import com.cyclic.models.game.Player;
import org.springframework.web.socket.WebSocketSession;

import java.util.Vector;

/**
 * Created by serych on 03.04.17.
 */
public class PlayerManager {
    private Vector<Player> players = new Vector<>();

    public Player getPlayerForSession(WebSocketSession session) {
        for (Player player : players) {
            if (player.getWebSocketSession() == session)
                return player;
        }
        return null;
    }

    public void createPlayer(WebSocketSession session) {
        players.add(new Player(session));
    }

    public void deletePlayer(WebSocketSession session) {
        for (Player player : players) {
            if (player.getWebSocketSession() == session) {
                player.delete();
                players.remove(player);
                return;
            }
        }
    }

    public void deletePlayer(Player player) {
        player.delete();
        players.remove(player);
    }
}
