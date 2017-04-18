package com.cyclic.models.game.net;

import static com.cyclic.controllers.WebSocketController.DATATYPE_ACCEPT_MOVE;

/**
 * Created by serych on 13.04.17.
 */
public class MoveAcceptBroadcast {
    private final int datatype = DATATYPE_ACCEPT_MOVE;
    private long id;
    private long nextid;

    public MoveAcceptBroadcast(long id, long nextid) {
        this.id = id;
        this.nextid = nextid;
    }
}
