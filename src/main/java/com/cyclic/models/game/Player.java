package com.cyclic.models.game;

/**
 * Created by serych on 01.04.17.
 */
public class Player {
    String nickname;
    int totalScore;


    public Player(String nickname) {
        this.nickname = nickname;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }
}
