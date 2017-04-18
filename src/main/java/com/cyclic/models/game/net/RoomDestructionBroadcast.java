package com.cyclic.models.game.net;

import com.cyclic.models.game.Moves;

import static com.cyclic.controllers.WebSocketController.DATATYPE_PLAYERMOVE;
import static com.cyclic.controllers.WebSocketController.DATATYPE_ROOM_DESTRUCTION;

/**
 * Created by serych on 03.04.17.
 */
public class RoomDestructionBroadcast {
    private final int datatype = DATATYPE_ROOM_DESTRUCTION;
}
