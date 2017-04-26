package com.cyclic.models.game.net;

import com.cyclic.models.game.Move;

import static com.cyclic.configs.Constants.DATATYPE_PLAYERMOVE;

/**
 * Created by serych on 03.04.17.
 */
public class PlayerMoveBroadcast {

    private final int datatype = DATATYPE_PLAYERMOVE;

    private long playerid;
    private long nextid;
    private Move move;

    public PlayerMoveBroadcast(long playerid, long nextid, Move move) {
        this.playerid = playerid;
        this.nextid = nextid;
        this.move = move;
    }
}
