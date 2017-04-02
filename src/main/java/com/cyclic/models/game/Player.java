package com.cyclic.models.game;

/**
 * Created by serych on 01.04.17.
 */
public class Player {
    String nickname;
    long id;
    long totalScore;
    long roomID;

    public Player(String nickname, long id, long totalScore) {
        this.nickname = nickname;
        this.id = id;
        this.totalScore = totalScore;
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
}
