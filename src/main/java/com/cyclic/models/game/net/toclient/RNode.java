package com.cyclic.models.game.net.toclient;

import com.cyclic.Application;

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

    public int getY() {
        return y;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj.getClass() != this.getClass())
            return false;
        RNode rNode = (RNode) obj;
        return x == rNode.x && y == rNode.y;
    }

    @Override
    public int hashCode() {
        return x * (Application.resourceManager.getRoomConfig().getFieldHeight() + 1) + y;
    }
}
