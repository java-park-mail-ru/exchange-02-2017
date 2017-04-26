package com.cyclic.models;

import com.cyclic.models.game.Move;

public class WebSocketAnswer {

    private Integer action = null;
    private Move move = null;

    public WebSocketAnswer(int action) {
        this.action = action;
    }

    public Move getMove() {
        return move;
    }

    public Integer getActionCode() {
        return action;
    }
}
