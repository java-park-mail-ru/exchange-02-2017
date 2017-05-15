package com.cyclic.models.game.net.toclient;

/**
 * Created by serych on 15.05.17.
 */
public class PlayerScore {
    private long pid;
    private long score;

    public PlayerScore(long pid, long score) {
        this.pid = pid;
        this.score = score;
    }
}
