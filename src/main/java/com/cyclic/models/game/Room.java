package com.cyclic.models.game;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.springframework.web.socket.WebSocketSession;

import java.io.IOException;
import java.util.Vector;

/**
 * Created by serych on 31.03.17.
 */
public class Room {
    public static final int STATUS_CREATING = 0;
    public static final int STATUS_PLAYING = 1;
    public static final int STATUS_READY = 2;

    public static final int PLAYERS_COUNT = 2;

    private Vector<Player> players = new Vector<>(4);
    private int status = STATUS_CREATING;
    private long roomID;

    public Room(long roomID) {
        this.roomID = roomID;
    }

    public int getSessionsCount() {
        return players.size();
    }

    public boolean addSession(WebSocketSession session) {
        if (status != STATUS_CREATING)
            return false;
        Player player = new Player(session);
        player.setNickname("Nick");
        player.setId(players.size());
        player.setRoomID(roomID);
        players.add(player);
        if (players.size() == PLAYERS_COUNT) {
            status = STATUS_READY;
        }
        broadcastRoomUpdate();
        return true;
    }

    private void broadcastRoomUpdate() {
        for (Player player: players) {
            Gson gson = new GsonBuilder().create();
            player.send(gson.toJson(players));
        }
    }

    public boolean start() {
        if (status == STATUS_READY) {
            status = STATUS_PLAYING;
            return true;
        }
        return false;
    }

    public int getStatus() {
        return status;
    }
}
