package com.cyclic.models.game.net;

import com.cyclic.models.game.Moves;

import static com.cyclic.controllers.WebSocketController.DATATYPE_PLAYERMOVE;

/**
 * Created by serych on 03.04.17.
 */
public class PlayerMoveBroadcast {
    public static final int MOVE_TEMP = 0;
    public static final int MOVE_NORM = 1;

    private final int datatype = DATATYPE_PLAYERMOVE;

    private long playerid;
    private int type;
    private Moves moves;

    public PlayerMoveBroadcast(long playerid, int type, Moves moves) {
        this.playerid = playerid;
        this.type = type;
        this.moves = moves;
    }
}
