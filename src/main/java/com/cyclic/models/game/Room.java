package com.cyclic.models.game;

import org.springframework.web.socket.WebSocketSession;

import java.util.Vector;

/**
 * Created by serych on 31.03.17.
 */
public class Room {
    public static final int STATUS_CREATING = 0;
    public static final int STATUS_PLAYING = 1;
    public static final int STATUS_READY = 2;

    public static final int PLAYERS_COUNT = 2;

    private Vector<WebSocketSession> sessions = new Vector<>(4);
    private int status = STATUS_CREATING;

    public int getSessionsCount() {
        return sessions.size();
    }

    public boolean addSession(WebSocketSession session) {
        if (status != STATUS_CREATING)
            return false;
        sessions.add(session);
        if (sessions.size() == PLAYERS_COUNT) {
            status = STATUS_READY;
        }
        return true;
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
