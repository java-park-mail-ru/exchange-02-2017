package com.cyclic.models.game;

import com.cyclic.LOG;
import com.cyclic.models.game.net.ConnectionError;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import java.io.IOException;

import static com.cyclic.models.game.net.ConnectionError.DISCONNECT_REASON_API_HACKER;

/**
 * Created by serych on 01.04.17.
 */
public class Player {

    private String nickname;
    private long id;
    private long units = Room.START_TOWER_UNITS;
    private int beginX = 0;
    private int beginY = 0;
    private transient Room room = null;
    private transient WebSocketSession webSocketSession;
    private transient Gson gson;
    private boolean readyForGameStart = false;

    public Player(WebSocketSession webSocketSession, String nickname, long id) {
        this.webSocketSession = webSocketSession;
        this.gson = new GsonBuilder().create();
        this.nickname = nickname;
        this.id = id;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public long getId() {
        return id;
    }

    public long getUnits() {
        return units;
    }

    public void setUnits(long units) {
        this.units = units;
    }

    public Room getRoom() {
        return room;
    }

    public void setRoom(Room room) {
        this.room = room;
    }

    public void sendString(String data) {
        try {
            webSocketSession.sendMessage(new TextMessage(data));
        } catch (IOException e) {
            LOG.error(e);
        }
    }

    public WebSocketSession getWebSocketSession() {
        return webSocketSession;
    }

    public void setWebSocketSession(WebSocketSession webSocketSession) {
        this.webSocketSession = webSocketSession;
    }

    public boolean isReadyForGameStart() {
        return readyForGameStart;
    }

    public void setReadyForGameStart(boolean readyForGameStart) {
        boolean changed = false;
        if (this.readyForGameStart != readyForGameStart)
            changed = true;
        this.readyForGameStart = readyForGameStart;
        if (room != null && changed)
            room.start();
    }

    public void disconnect(int code, String data) {
        sendString(gson.toJson(new ConnectionError(code, data)));
        try {
            webSocketSession.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void disconnectBadApi() {
        LOG.errorConsole(nickname + " is hacker! Ip: " + getWebSocketSession().getRemoteAddress() + ". Kick him!");
        disconnect(DISCONNECT_REASON_API_HACKER, "");
    }

    public void disconnectBadApi(String reason) {
        LOG.errorConsole(nickname + " is hacker! Ip: " + getWebSocketSession().getRemoteAddress() + ". Reason: " + reason);
        disconnect(DISCONNECT_REASON_API_HACKER, reason);
    }

    @Override
    public int hashCode() {
        return nickname.hashCode();
    }

    public int getBeginX() {
        return beginX;
    }

    public void setBeginX(int beginX) {
        this.beginX = beginX;
    }

    public int getBeginY() {
        return beginY;
    }

    public void setBeginY(int beginY) {
        this.beginY = beginY;
    }
}
