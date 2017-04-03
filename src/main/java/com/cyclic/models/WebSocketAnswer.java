package com.cyclic.models;

import com.cyclic.models.game.Move;

import java.util.Vector;

public class WebSocketAnswer {

    public static final int READY_FOR_ROOM_SEARCH = 1;
    public static final int READY_FOR_GAME_START = 2;
    public static final int GAME_MOVE = 3;

    /**
     * ErrorCode
     */
    private int ec = 0;
    /**
     * ErrorSting
     */
    private String es = null;
    /**
     * ActionCode
     */
    private int ac = 0;
    private Vector<Move> moves = null;

    public WebSocketAnswer(int ec, String es, int ac) {
        this.ec = ec;
        this.es = es;
        this.ac = ac;
    }

    public Vector<Move> getMoves() {
        return moves;
    }

    public int getErrorCode() {
        return ec;
    }

    public String getErrorString() {
        return es;
    }

    public int getActionCode() {
        return ac;
    }
}
