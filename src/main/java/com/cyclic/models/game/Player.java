package com.cyclic.models.game;

import com.cyclic.models.game.net.ConnectionError;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import java.io.IOException;
import java.util.concurrent.ThreadLocalRandom;

import static com.cyclic.models.game.net.ConnectionError.DISCONNECT_REASON_API_HACKER;

/**
 * Created by serych on 01.04.17.
 */
public class Player {

    private String nickname;
    private long id;
    private long totalScore = 0;
    private long beginX = 0;
    private long beginY = 0;
    private transient Room room = null;
    private transient WebSocketSession webSocketSession;
    private transient Gson gson;
    private boolean readyForGameStart = false;

    public Player(WebSocketSession webSocketSession, String nickname, long id) {
        beginX = ThreadLocalRandom.current().nextInt(0, Room.FIELD_WIDTH);
        beginY = ThreadLocalRandom.current().nextInt(0, Room.FIELD_HEIGHT);
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

    public void sendString(String data) {
        try {
            webSocketSession.sendMessage(new TextMessage(data));
        } catch (IOException e) {
            e.printStackTrace();
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
        disconnect(DISCONNECT_REASON_API_HACKER, "");
    }

    @Override
    public int hashCode() {
        return nickname.hashCode();
    }
}
