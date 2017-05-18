package com.cyclic.models.game.net.fromclient;

import com.cyclic.configs.Enums;
import com.cyclic.models.game.net.fromclient.Move;

public class WebSocketAnswer {

    private Enums.Action action = null;
    private Move move = null;
    private Integer roomCapacity = null;

    public Enums.Action getAction() {
        return action;
    }

    public Move getMove() {
        return move;
    }

    public Integer getRoomCapacity() {
        return roomCapacity;
    }
}
