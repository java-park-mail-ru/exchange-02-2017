package com.cyclic.models.game.net.toclient.broadcast;

import com.cyclic.configs.Enums;
import com.cyclic.models.game.Node;

import java.util.ArrayList;

import static com.cyclic.configs.Enums.Datatype.DATATYPE_NEWBONUS;

/**
 * Created by serych on 05.04.17.
 */
public class NewBonusBroadcast {
    private final Enums.Datatype datatype = DATATYPE_NEWBONUS;
    private ArrayList<Node> bonuses;

    public NewBonusBroadcast(ArrayList<Node> bonuses) {
        this.bonuses = bonuses;
    }
}
