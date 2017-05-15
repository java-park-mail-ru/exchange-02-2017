package com.cyclic.models.game.net.toclient;

/**
 * Created by serych on 12.05.17.
 */
public class RNode {
    private int x;
    private int y;

    public RNode(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }
}
