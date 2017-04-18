package com.cyclic.models.game.net;

import com.cyclic.models.game.Bonus;

import java.util.Vector;

import static com.cyclic.controllers.WebSocketController.DATATYPE_NEWBONUS;
import static com.cyclic.controllers.WebSocketController.DATATYPE_ROOMINFO;

/**
 * Created by serych on 05.04.17.
 */
public class NewBonusBroadcast {
    private final int datatype = DATATYPE_NEWBONUS;
    private Vector<Bonus> bonuses;

    public NewBonusBroadcast(Vector<Bonus> bonuses) {
        this.bonuses = bonuses;
    }
}
