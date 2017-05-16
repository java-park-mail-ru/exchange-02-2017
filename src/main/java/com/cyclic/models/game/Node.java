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
    private long pid;
    private int value;
    private int x;
    private int y;

    public Node(long pid, int value, int x, int y) {
        this.value = value;
        this.pid = pid;
        this.x = x;
        this.y = y;
    }

    public Node(int value, int x, int y) {
        this.value = value;
        this.pid = -1;
        this.x = x;
        this.y = y;
    }


    public long getPid() {
        return pid;
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
        return pid == -1;
    }
}
