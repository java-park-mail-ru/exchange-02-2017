package com.cyclic.models.base;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;
import java.util.Vector;

/**
 * Created by serych on 19.05.17.
 */
public class ScoreBoard {

    private Vector<PlayerScore> scoreBoard;
    private long page;

    public ScoreBoard(List<User> usersToAdd, long page) {
        scoreBoard = new Vector<>();
        usersToAdd.forEach(user -> {
            scoreBoard.add(new PlayerScore(user.getLogin(), user.getHighScore()));
        });
        this.page = page;
    }

    public class PlayerScore {
        private String nick;
        private long highscore;

        public PlayerScore(String nick, long highscore) {
            this.nick = nick;
            this.highscore = highscore;
        }
    }


}
