package com.cyclic.models.game.net;

import static com.cyclic.controllers.WebSocketController.DATATYPE_PLAYER_DISCONNECT;

/**
 * Created by serych on 13.04.17.
 */
public class PlayerDisconnectBroadcast {
    private final int datatype = DATATYPE_PLAYER_DISCONNECT;
    private long id;

    public PlayerDisconnectBroadcast(long id) {
        this.id = id;
    }
}
