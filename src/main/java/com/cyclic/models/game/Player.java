package com.cyclic.models.game;

import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import java.io.IOException;

/**
 * Created by serych on 01.04.17.
 */
public class Player {

    public static final int DISCONNECT_REASON_API_HACKER = 1;

    private String nickname = "";
    private long id = 0;
    private long totalScore = 0;
    private transient Room room = null;
    private transient WebSocketSession webSocketSession;
    private boolean readyForGameStart = false;

    public Player(WebSocketSession webSocketSession) {
        this.webSocketSession = webSocketSession;
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

    public void setId(long id) {
        this.id = id;
    }

    public long getTotalScore() {
        return totalScore;
    }

    public void setTotalScore(long totalScore) {
        this.totalScore = totalScore;
    }

    public Room getRoom() {
        return room;
    }

    public void setRoom(Room room) {
        this.room = room;
    }

    public void send(String data) {
        try {
            webSocketSession.sendMessage(new TextMessage(data));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public WebSocketSession getWebSocketSession() {
        return webSocketSession;
    }

    public void delete() {

    }

    public void setWebSocketSession(WebSocketSession webSocketSession) {
        this.webSocketSession = webSocketSession;
    }

    public boolean isReadyForGameStart() {
        return readyForGameStart;
    }

    public void setReadyForGameStart(boolean readyForGameStart) {
        this.readyForGameStart = readyForGameStart;
        if (room != null)
            room.start();
    }

    public void disconnect(int reason) {
        send("{disconnectreason:" + reason + "}");
        try {
            webSocketSession.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void disconnectBadApi() {
        disconnect(DISCONNECT_REASON_API_HACKER);
    }
}
