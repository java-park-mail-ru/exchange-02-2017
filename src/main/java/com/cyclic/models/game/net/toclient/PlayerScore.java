package com.cyclic.models.game.net.toclient;

/**
 * Created by serych on 15.05.17.
 */
public class PlayerScore {
    private long pid;
    private long score;
    private long towers;

    public PlayerScore(long pid, long score, long towers) {
        this.pid = pid;
        this.score = score;
        this.towers = towers;
    }

    public long getScore() {
        return score;
    }
}
