package com.cyclic.models.game.net;

import com.cyclic.models.game.Move;

import java.util.Vector;

/**
 * Created by serych on 03.04.17.
 */
public class PlayerMoveAnswer {
    private long playerid;
    private Vector<Move> moves;

    public PlayerMoveAnswer(long playerid, Vector<Move> moves) {
        this.playerid = playerid;
        this.moves = moves;
    }
}
