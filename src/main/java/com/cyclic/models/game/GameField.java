package com.cyclic.models.game;

import com.cyclic.models.game.moves.CreateMove;
import com.cyclic.models.game.net.NewBonusBroadcast;

import java.awt.*;
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

    public Node addNewTower(Node src, Node dst) {
        if (src.getType() != Node.NODE_TOWER || dst.getType() != Node.NODE_TOWER) {
            return null;
        }
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

    public Node addRandomBonus() {

        ThreadLocalRandom.current().nextInt(Room.BONUS_MIN_VALUE, Room.BONUS_MAX_VALUE + 1);

        Point point = findRandomNullPoint();
        if (point == null)
            return null;
        Node node = new Node(null,
                0,
                ThreadLocalRandom.current().nextInt(Room.BONUS_MIN_VALUE, Room.BONUS_MAX_VALUE + 1),
                Node.NODE_BONUS, point.x, point.y
                );

        world[point.x][point.y] = node;
        room.broadcast(room.getGson().toJson(new NewBonusBroadcast(point.x, point.y, node.getValue())));
        return node;
    }

    public Point findRandomNullPoint() {
        int x = 0, y = 0;
        // try to find random point 10 times
        for (int i = 0; i < 10; i++) {
            x = ThreadLocalRandom.current().nextInt(0, width + 1);
            y = ThreadLocalRandom.current().nextInt(0, height + 1);
            if (world[x][y] == null) {
                return new Point(x, y);
            }
        }
        // go layer by layer to find free point
        for (int i = 0; i < width * height; i++) {
            x++;
            if (x == width) {
                x = 0;
                y++;
            }
            if (y == height) {
                y = 0;
            }
            if (world[x][y] == null) {
                return new Point(x, y);
            }
        }
        // reaches if there is NO free point. If during production this code will go, tell it to @SCaptainCAP. I'll buy you shaurma
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
                        createMove.getUnitsCount(), createMove.getType(),
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
