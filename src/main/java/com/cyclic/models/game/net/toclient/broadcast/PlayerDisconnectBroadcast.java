package com.cyclic.models.game.net.toclient.broadcast;

import com.cyclic.configs.Enums;
import com.cyclic.models.game.Node;
import com.cyclic.models.game.Player;
import com.cyclic.models.game.net.toclient.NodesLink;
import com.cyclic.models.game.net.toclient.RNode;

import java.util.HashSet;
import java.util.Vector;

import static com.cyclic.configs.Enums.Datatype.DATATYPE_PLAYER_DISCONNECT;

/**
 * Created by serych on 13.04.17.
 */
public class PlayerDisconnectBroadcast {
    private final Enums.Datatype datatype = DATATYPE_PLAYER_DISCONNECT;
    private long pid;
    private Long giveMoveToPid;

    public PlayerDisconnectBroadcast(Player player, Long giveMoveToPid) {
        this.pid = player.getId();
        this.giveMoveToPid = giveMoveToPid;
    }
}
