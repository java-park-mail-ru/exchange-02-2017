package com.cyclic.models.game;

import com.cyclic.models.game.net.broadcast.MoveBroadcast;
import com.cyclic.models.game.net.broadcast.NewBonusBroadcast;
import com.cyclic.models.game.net.fromclient.Move;
import com.cyclic.models.game.net.toclient.RNode;
import com.sun.istack.internal.Nullable;

import java.awt.*;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

import static com.cyclic.configs.Enums.MoveResult.*;

/**
 * Created by serych on 03.04.17.
 */
public class GameField {
    private final static Random randomDelta = new Random(); // TODO check
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
        this.width = width;

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
        Vector<Node> bonuses = new Vector<>();
        for (int i = 0; i < count; i++) {
            Point point = findRandomNullPoint();
            if (point == null)
                return;
            Node node = new Node(
                    ThreadLocalRandom.current().nextInt(room.getBonusMinValue(), room.getBonusMaxValue() + 1),
                    point.x,
                    point.y
            );
            world[point.y][point.x] = node;
            bonuses.add(node);
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

    /**
     * This method need garant that user did not have new node and had parentNode!!!
     */

    private Node addNewTower(Node parentNode, Player player, int unitsCount, Move move) {
        int x = move.getXto();
        int y = move.getYto();
        Node node = new Node(
                player.getId(),
                unitsCount,
                x, y);
        HashMap<Node, HashSet<Node>> nodesMap = player.getNodesMap();
        nodesMap.get(parentNode).add(node);
        HashSet<Node> hashSet = new HashSet<>();
        hashSet.add(parentNode);
        nodesMap.put(node, hashSet);
        setNodeToPosition(x, y, node);
        return node;
    }

    private MoveBroadcast playerMoveFree(Player player, Node fromNode, Move move) {
        int moveUnits = move.getUnitsCount();
        if (!(moveUnits >= 1 && moveUnits < fromNode.getValue())) {
            return null;
        }
        fromNode.addToValue(-moveUnits);

        Node newNode = addNewTower(fromNode, player, moveUnits, move);

        MoveBroadcast moveBroadcast = new MoveBroadcast();
        moveBroadcast.setResult(ACCEPT_OK);
        moveBroadcast.addNewNode(newNode);
        moveBroadcast.addValueUpdate(fromNode);

        return moveBroadcast;
    }

    private MoveBroadcast playerMoveBonus(Player player, Node fromNode, Node bonus, Move move) {
        int moveUnits = move.getUnitsCount();
        fromNode.addToValue(-moveUnits);
        moveUnits += bonus.getValue();

        Node newNode = addNewTower(fromNode, player,
                moveUnits, move);


        MoveBroadcast moveBroadcast = new MoveBroadcast();
        moveBroadcast.setResult(ACCEPT_OK);
        moveBroadcast.addNewNode(newNode);
        moveBroadcast.addRemovedNode(newNode.getReduced());
        moveBroadcast.addValueUpdate(fromNode);

        return moveBroadcast;
    }

    private MoveBroadcast playerMoveLink(Player player, Node fromNode, Node toNode, Move move) {
        int moveUnits = move.getUnitsCount();
        fromNode.addToValue(-moveUnits);
        toNode.addToValue(moveUnits);

        HashMap<Node, HashSet<Node>> nodesMap = player.getNodesMap();
        nodesMap.get(fromNode).add(toNode);
        nodesMap.get(toNode).add(fromNode);

        MoveBroadcast moveBroadcast = new MoveBroadcast();
        moveBroadcast.setResult(ACCEPT_OK);
        moveBroadcast.addNewLink(fromNode, toNode);
        moveBroadcast.addValueUpdate(fromNode);

        return moveBroadcast;
    }

    private MoveBroadcast playerMoveAttack(Player player, Node fromNode, Node enemyNode, Move move) {
        int moveUnits = move.getUnitsCount();
        fromNode.addToValue(-moveUnits);

        int delta = enemyNode.getValue() - moveUnits;
        /*
         *  delta < 0 ~ current Player win
         *  delta = 0 ~ Random
         *  delta > 0 ~ enemy win
         */

        if (delta == 0) {
            delta = (randomDelta.nextInt(1)) == 0 ? 1 : -1;
        }

        MoveBroadcast moveBroadcast = new MoveBroadcast();

        Vector<RNode> deletedNodes;
        if (delta > 0) {
            deletedNodes = killNode(fromNode);
            enemyNode.addToValue(-moveUnits);
            moveBroadcast.addValueUpdate(enemyNode);
            moveBroadcast.setResult(ACCEPT_LOSE);
        } else {
            deletedNodes = killNode(enemyNode);
            fromNode.addToValue(-moveUnits);
            moveBroadcast.addValueUpdate(fromNode);
            moveBroadcast.setResult(ACCEPT_WIN);
        }

        moveBroadcast.setRemovedNodes(deletedNodes);

        return moveBroadcast;
    }

    public Vector<RNode> killNode(Node node) {
        if (node == null)
            return null;
        world[node.getY()][node.getX()] = null;
        if (node.isBonus()) {
            Vector<RNode> deleteNodes = new Vector<>();
            deleteNodes.add(node.getReduced());
            return deleteNodes;
        } else {
            Player player = room.getPlayer(node.getPlayerID());

            // If killed main player's node
            // Remove player from game
            if (player.getMainNode() == node) {
                room.removePlayer(player);
                Vector<Node> deleteNodes = player.getNodes();
                deleteNodes.forEach(n -> {
                    setNodeToPosition(n.getX(), n.getY(), null);
                });
                return player.getReducedNodes();
            }
            HashMap<Node, HashSet<Node>> nodesMap = player.getNodesMap();

            // remove THIS node from map
            if (nodesMap.containsKey(node)) {
                for (Node n : nodesMap.get(node)) {
                    nodesMap.get(n).remove(node);
                }
                nodesMap.remove(node);
            }

            // Create map of visited nodes in DFS
            HashMap<Node, Boolean> visitedNodes = new HashMap<>();
            nodesMap.keySet().forEach(n -> {
                visitedNodes.put(n, false);
            });
            visitedNodes.put(player.getMainNode(), true);


            // DFS
            Stack<Node> s = new Stack<>();
            s.push(player.getMainNode());
            while (!s.empty()) {
                Node v = s.pop();
                for (Node n : nodesMap.get(v)) {
                    if (!visitedNodes.get(n)) {
                        s.push(n);
                        visitedNodes.put(n, true);
                    }
                }
            }
            // DFS

            // Now we have visitedNodes map. If node was not visited,
            // remove it from world and add to returning array
            Vector<RNode> deleteNodes = new Vector<>();
            deleteNodes.add(node.getReduced());
            visitedNodes.forEach((n, visited) -> {
                if (!visited) {
                    for (Node v : nodesMap.get(n)) {
                        nodesMap.get(v).remove(n);
                    }
                    nodesMap.remove(n);
                    deleteNodes.add(n.getReduced());
                    setNodeToPosition(n.getX(), n.getY(), null);
                }
            });
            return deleteNodes;
        }
    }

    public void removeNode(Node node) {
        if (node == null)
            return;
        if (node.getX() < 0 || node.getX() >= width || node.getY() < 0 || node.getY() >= height)
            return;
        world[node.getY()][node.getX()] = null;
    }

    @Nullable
    public MoveBroadcast acceptMove(Player player, Move move) {
        Node my = getByPosition(move.getXfrom(), move.getYfrom());
        Node target = getByPosition(move.getXto(), move.getYto());
        MoveBroadcast moveBroadcast = null;

        if (checkTurn(my)) {
            if (target == null) {
                moveBroadcast = playerMoveFree(player, my, move);
            } else if (checkTurn(target)) {
                moveBroadcast = playerMoveLink(player, my, target, move);
            } else {
                if (target.isBonus())
                    moveBroadcast = playerMoveBonus(player, my, target, move);
                else
                    moveBroadcast = playerMoveAttack(player, my, target, move);
            }
        }
        return moveBroadcast;
    }
}
