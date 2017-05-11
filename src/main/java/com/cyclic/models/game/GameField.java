package com.cyclic.models.game;

import com.cyclic.models.game.net.NewBonusBroadcast;

import java.awt.*;
import java.util.Random;
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

    public void setNodeToPosition(int beginX, int beginY, Node node) {
        if (beginX < 0 || beginX >= width || beginY < 0 || beginY >= height)
            return;
        world[beginY][beginX] = node;
    }

    public void addAndBroadcastRandomBonuses(int count) {
        Vector<Bonus> bonuses = new Vector<>();
        for (int i = 0; i < count; i++) {
            Point point = findRandomNullPoint();
            if (point == null)
                return;
            Node node = new Node(null,
                    0,
                    ThreadLocalRandom.current().nextInt(room.getBonusMinValue(), room.getBonusMaxValue() + 1),
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

    private boolean checkTurn(Node node) {
        return node != null && node.getPlayerID() == room.getPid();
    }

    private void addNewTower(Node parentNode, Long pid, int unitsCount, int x, int y) {
        setNodeToPosition(x, y, new Node(
                parentNode,
                pid,
                unitsCount,
                NODE_TOWER,
                x, y));
    }

    private void addNewTower(Node parentNode, Long pid, int unitsCount, Move move) {
        addNewTower(parentNode, pid, unitsCount, move.getXto(), move.getXto());
    }

    private void playerMoveFree(Node fromNode, Move move) {
        int moveUnits = move.getUnitsCount();
        if (moveUnits < 1 || moveUnits >= fromNode.getValue()) {
            move.setType(Move.MoveType.ACCEPT_FAIL);
            return;
        }
        fromNode.addToValue(-moveUnits);

        addNewTower(fromNode, fromNode.getPlayerID(), moveUnits, move);
        move.setParentUnitsCount(fromNode.getValue());
        move.setType(Move.MoveType.ACCEPT_OK);
    }

    private void playerMoveBonus(Node fromNode, Node bonus, Move move) {
        int moveUnits = move.getUnitsCount();
        fromNode.addToValue(-moveUnits);
        moveUnits += bonus.getValue();

        /* rewrite bonus */
        addNewTower(fromNode, fromNode.getPlayerID(),
                moveUnits, bonus.getX(), bonus.getY());

        move.setType(Move.MoveType.ACCEPT_OK);
    }

    private void playerMoveLink(Node fromNode, Node toNode, Move move) {
        int moveUnits = move.getUnitsCount();
        fromNode.addToValue(-moveUnits);

        addNewTower(fromNode, fromNode.getPlayerID(), moveUnits, move);
        move.setParentUnitsCount(fromNode.getValue());
        move.setType(Move.MoveType.ACCEPT_OK);
    }

    private final static Random randomDelta = new Random(); // TODO check
    private void playerMoveAttack(Node fromNode, Node enemyNode, Move move) {
        int moveUnits = move.getUnitsCount();
        fromNode.addToValue(-moveUnits);

        int delta = enemyNode.getValue() - moveUnits;
        /*
         *  delta < 0 ~ current Player win
         *  delta = 0 ~ Random
         *  delta > 0 ~ enemy win
         */

        if(delta == 0) {
            delta = (randomDelta.nextInt(1)) == 0 ? 1 : -1;
        }

        if(delta > 0) {
            killNode(fromNode);
            enemyNode.addToValue(-moveUnits);
            move.setType(Move.MoveType.ACCEPT_LOST);
        } else {
            killNode(enemyNode);
            fromNode.addToValue(-moveUnits);
            move.setType(Move.MoveType.ACCEPT_WIN);
        }
    }

    public void killNode(Node node) {
        if (node == null)
            return;
        if (node.getType() == NODE_BONUS) {
            world[node.getY()][node.getX()] = null;
        }
        if (node.getType() == NODE_TOWER) {
            Player player = room.getPlayer(node.getPlayerID());
            if (player.getMainNode() == node) {
                room.removePlayer(player);
                return;
            }
            for (Node testNode : player.getNodesMap().get(player.getMainNode())) {

            }
        }
    }

    public void removeNode(Node node) {
        if (node == null)
            return;
        if (node.getX() < 0 || node.getX() >= width || node.getY() < 0 || node.getY() >= height)
            return;
        world[node.getY()][node.getX()] = null;
    }

    public void acceptMove(Move move) {
        Node my = getByPosition(move.getXfrom(), move.getYfrom());
        Node target = getByPosition(move.getXto(), move.getYto());

        if (checkTurn(my)) {
            if(target == null) {
                playerMoveFree(my, move);
            }
            else if (checkTurn(target)) {
                playerMoveLink(my, target, move);
            }
            else {
                switch (target.getType()) {
                    case NODE_TOWER:
                        playerMoveAttack(my, target, move);
                        break;
                    case NODE_BONUS:
                        playerMoveBonus(my, target, move);
                }
            }
            move.setParentUnitsCount(my.getValue());
        }
        else {
            move.setType(Move.MoveType.ACCEPT_FAIL);
        }
    }
}
