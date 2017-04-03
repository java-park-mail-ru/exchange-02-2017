package com.cyclic.models.game.net;

import com.cyclic.models.game.Moves;

import java.util.Vector;

/**
 * Created by serych on 03.04.17.
 */
public class PlayerMoveAnswer {
    public static final int ANSWER_MOVE_TEMP = 0;
    public static final int ANSWER_MOVE_NORM = 1;

    private long playerid;
    private long type;
    private Moves moves;

    public PlayerMoveAnswer(long playerid, long type, Moves moves) {
        this.playerid = playerid;
        this.type = type;
        this.moves = moves;
    }
}
