package com.cyclic.models.game;

import com.cyclic.models.game.net.NewBonusBroadcast;

import java.awt.*;
import java.util.Vector;
import java.util.concurrent.ThreadLocalRandom;

import static com.cyclic.configs.Constants.NODE_BONUS;
import static com.cyclic.configs.Constants.NODE_TOWER;

/**
 * Created by serych on 03.04.17.
 */
public class GameField {
    private transient int height, width;
    private transient Node[][] world;
    private transient Room room;


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
        return world[y][x];
    }

//    public Node addNewTower(Node src, Node dst) {
//        if (src.getType() != Node.NODE_TOWER || dst.getType() != Node.NODE_TOWER) {
//            return null;
//        }
//        int newX = dst.getX();
//        int newY = dst.getY();
//
//        if(src.getX() == newX && src.getY() == newY)
//            return null;
//
//        if(getByPosition(newX, newY) == null) {
//            src.addChild(dst);
//            world[newX][newY] = dst;
//            return dst;
//        }
//        return null;
//    }

    public void addAndBroadcastRandomBonuses(int count) {
        Vector<Bonus> bonuses = new Vector<>();
        for (int i = 0; i < count; i++) {
            Point point = findRandomNullPoint();
            if (point == null)
                return;
            Node node = new Node(null,
                    0,
                    ThreadLocalRandom.current().nextInt(Room.BONUS_MIN_VALUE, Room.BONUS_MAX_VALUE + 1),
                    NODE_BONUS, point.x, point.y
            );
            world[point.y][point.x] = node;
            bonuses.add(new Bonus(point.x, point.y, node.getValue()));
        }
        room.broadcast(room.getGson().toJson(new NewBonusBroadcast(bonuses)));
    }

    public Point findRandomNullPoint() {
        int x = 0, y = 0;
        // try to find random point 10 times
        for (int i = 0; i < 10; i++) {
            x = ThreadLocalRandom.current().nextInt(0, width);
            y = ThreadLocalRandom.current().nextInt(0, height);
            if (world[y][x] == null) {
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
            if (world[y][x] == null) {
                return new Point(x, y);
            }
        }
        // reaches if there is NO free point. If during production this code will go, tell it to @SCaptainCAP. I'll buy you shaurma
        return null;
    }

    public void stopGame() {
        room = null;
    }

    public boolean acceptMove(Player player, Move move) {
        Node n1 = getByPosition(move.getXfrom(), move.getYfrom());
        Node n2 = getByPosition(move.getXto(), move.getYto());
        if (n1 != null && n1.getPlayerID() == room.getPid()) {
            if (n2 != null && n2.getType() == NODE_TOWER)
                return false;
            int newUnits = n1.getValue();
            if (n2 != null && n2.getType() == NODE_BONUS) {
                newUnits = n2.getValue();
            } else {
                newUnits /= 2;
                n1.setValue(n1.getValue() - newUnits);
            }
            move.setParentUnitsCount(n1.getValue());
            setNodeToPosition(move.getXto(), move.getYto(), new Node(
                    n1,
                    room.getPid(),
                    newUnits,
                    NODE_TOWER,
                    move.getXto(),
                    move.getYto()));

            return true;
        }
        return false;
    }

    public void setNodeToPosition(int beginX, int beginY, Node node) {
        if (beginX < 0 || beginX >= width || beginY < 0 || beginY >= height)
            return;
        world[beginY][beginX] = node;
    }
}
