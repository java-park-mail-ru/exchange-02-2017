package com.cyclic.models.game;

import java.util.Random;
import java.util.Vector;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Created by serych on 03.04.17.
 */
public class GameField {
    private transient int height, width;
    private transient Node[][] world;
    private transient Room room;
    private int performingId;

    public GameField(Room room, int height, int width) {
        this.room = room;
        world = new Node[height][width];
        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                world[i][j] = null;
            }
        }
        this.height = height;
        this.width  = width;
        this.performingId = ThreadLocalRandom.current().nextInt(0, Room.PLAYERS_COUNT);
    }

    public Node getByPosition(int x, int y) {
        return world[x][y];
    }

    public Node addNewNode(Node src, Node dst) {
        int newX = dst.getX();
        int newY = dst.getY();

        if(src.getX() == newX && src.getY() == newY)
            return null;

        if(getByPosition(newX, newY) == null) {
            src.addChild(dst);
            world[newX][newY] = dst;
            return dst;
        }
        return null;
    }

    public void stopGame() {
        room = null;
    }

    public int getPerformingId() {
        return performingId;
    }
}
