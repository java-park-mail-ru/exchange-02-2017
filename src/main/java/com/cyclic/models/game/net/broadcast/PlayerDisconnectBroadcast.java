package com.cyclic.models.game.net.broadcast;

import com.cyclic.configs.Enums;

import static com.cyclic.configs.Enums.Datatype.DATATYPE_PLAYER_DISCONNECT;

/**
 * Created by serych on 13.04.17.
 */
public class PlayerDisconnectBroadcast {
    private final Enums.Datatype datatype = DATATYPE_PLAYER_DISCONNECT;
    private long pid;

    public PlayerDisconnectBroadcast(long pid) {
        this.pid = pid;
    }
}
