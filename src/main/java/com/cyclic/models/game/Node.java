package com.cyclic.models.game;

import com.cyclic.models.game.net.toclient.RNode;
import org.jetbrains.annotations.NotNull;

/**
 * Created by serych on 03.04.17.
 */
public class Node {

    /**
     * If -1, it is BONUS
     */
    private long playerID;
    private int value;
    private int x;
    private int y;

    public Node(long playerID, int value, int x, int y) {
        this.value = value;
        this.playerID = playerID;
        this.x = x;
        this.y = y;
    }

    public Node(int value, int x, int y) {
        this.value = value;
        this.playerID = -1;
        this.x = x;
        this.y = y;
    }


    public long getPlayerID() {
        return playerID;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public int getValue() {
        return value;
    }

    public void addToValue(int inc) {
        this.value += inc;
    }

    @NotNull
    public RNode getReduced() {
        return new RNode(x, y);
    }

    public boolean isBonus() {
        return playerID == -1;
    }
}
