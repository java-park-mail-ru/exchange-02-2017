package com.cyclic.models.game;

import com.cyclic.models.game.moves.CreateMove;

import java.util.concurrent.ThreadLocalRandom;

/**
 * Created by serych on 03.04.17.
 */
public class GameField {
    private transient int height, width;
    private transient Node[][] world;
    private transient Room room;
    private transient Moves possibleMoves;

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

    public Moves getPossibleMoves() {
        return possibleMoves;
    }

    public void setPossibleMoves(Moves possibleMoves) {
        this.possibleMoves = possibleMoves;
    }

    public boolean acceptMove() {
        for (CreateMove createMove : possibleMoves.getCreateMoves()) {
            Node n1 = world[createMove.getXfrom()][createMove.getYfrom()];
            Node n2 = world[createMove.getXto()][createMove.getYto()];
            if (n1 != null && n1.getPlayerID() == room.getPerformingId() && n2 == null) {
                world[createMove.getXto()][createMove.getYto()] = new Node(
                        n1,
                        room.getPerformingId(),
                        createMove.getType(),
                        createMove.getXto(),
                        createMove.getYto());
            }
            else {
                return false;
            }
        }
        return true;
    }
}
