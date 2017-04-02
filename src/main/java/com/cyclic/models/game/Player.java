package com.cyclic.models.game;

import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import java.io.IOException;

/**
 * Created by serych on 01.04.17.
 */
public class Player {
    private String nickname = "";
    private long id = 0;
    private long totalScore = 0;
    private long roomID = 0;
    private transient WebSocketSession webSocketSession;

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

    public long getRoomID() {
        return roomID;
    }

    public void setRoomID(long roomID) {
        this.roomID = roomID;
    }

    public void send(String data) {
        try {
            webSocketSession.sendMessage(new TextMessage(data));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
