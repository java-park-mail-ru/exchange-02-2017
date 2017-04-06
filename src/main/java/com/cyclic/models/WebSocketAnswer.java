package com.cyclic.models;

import com.cyclic.models.game.Moves;

public class WebSocketAnswer {

    public static final int READY_FOR_ROOM_SEARCH = 1;
    public static final int READY_FOR_GAME_START = 2;
    public static final int GAME_UPDATE_MY_MOVE = 3;
    public static final int GAME_ACCEPT_MY_MOVE = 4;

    /**
     * ActionCode
     */
    private Integer action = null;
    private Moves moves = null;

    public WebSocketAnswer(int action) {
        this.action = action;
    }

    public Moves getMoves() {
        return moves;
    }

    public Integer getActionCode() {
        return action;
    }
}
