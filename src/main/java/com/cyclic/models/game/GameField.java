package com.cyclic.models.game;

import com.cyclic.LOG;
import com.cyclic.models.game.net.broadcast.MoveBroadcast;
import com.cyclic.models.game.net.broadcast.NewBonusBroadcast;
import com.cyclic.models.game.net.fromclient.Move;
import com.cyclic.models.game.net.toclient.NodesLink;
import com.cyclic.models.game.net.toclient.RNode;
import com.sun.istack.internal.NotNull;
import com.sun.istack.internal.Nullable;
import com.sun.rowset.internal.Row;

import javax.validation.constraints.Null;
import java.awt.*;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

import static com.cyclic.configs.Enums.MoveResult.*;

/**
 * Created by serych on 03.04.17.
 */
public class GameField {
    private transient int height, width;
    private transient Node[][] world;
    private transient Vector<Point> freePoints;
    private transient Room room;

    public GameField(Room room) {
        this.room = room;
        this.height = room.getFieldHeight();
        this.width = room.getFieldWidth();
        world = new Node[height][width];
        freePoints = new Vector<>();
        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                freePoints.add(new Point(i, j));
            }
        }
    }

    public Node getByPosition(int x, int y) {
        return world[y][x];
    }

    public void setNodeToPosition(int x, int y, Node node) {
        if (x < 0 || x >= width || y < 0 || y >= height) {
            LOG.error(new Exception("Cannot set node to position! Index is out of range!"));
        }
        if (node == null) {
            freePoints.add(new Point(x,y));
        }
        else {
            freePoints.remove(new Point(x, y));
        }
        world[y][x] = node;
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
            setNodeToPosition(point.x, point.y, node);
            bonuses.add(node);
        }
        room.broadcast(room.getGson().toJson(new NewBonusBroadcast(bonuses)));
    }

    @Nullable
    public Point findRandomNullPoint() {
        if (freePoints.size() == 0)
            return null;
        int rand = ThreadLocalRandom.current().nextInt(0, freePoints.size());
        return freePoints.get(rand);
    }

    private boolean checkTurn(Node node) {
        return node != null && node.getPid() == room.getPid();
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
        moveBroadcast.addNewLink(fromNode, newNode);
        moveBroadcast.addValueUpdate(fromNode);

        return moveBroadcast;
    }

    private MoveBroadcast playerMoveUnitsMove(Player player, Node fromNode, Node toNode, Move move) {
        int moveUnits = move.getUnitsCount();
        if (!(moveUnits >= 1 && moveUnits < fromNode.getValue())) {
            return null;
        }
        fromNode.addToValue(-moveUnits);
        toNode.addToValue(moveUnits);

        MoveBroadcast moveBroadcast = new MoveBroadcast();
        moveBroadcast.setResult(ACCEPT_OK);
        moveBroadcast.addValueUpdate(fromNode);
        moveBroadcast.addValueUpdate(toNode);

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
        moveBroadcast.addNewLink(fromNode, newNode);
        //moveBroadcast.addRemovedNode(newNode.getReduced());
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
        Player enemy = room.getPlayer(enemyNode.getPid());
        /*
         *  delta < 0 ~ current Player win
         *  delta = 0 ~ Random
         *  delta > 0 ~ enemy win
         */

        if (delta == 0) {
            delta = ThreadLocalRandom.current().nextInt(0, 2) == 0 ? 1 : -1;
        }

        MoveBroadcast moveBroadcast = new MoveBroadcast();
        Node newNode;
        NodesAndLinks deleted;
        if (delta > 0) {
            enemyNode.addToValue(-moveUnits);
            moveBroadcast.addValueUpdate(enemyNode);
            moveBroadcast.addValueUpdate(fromNode);
            moveBroadcast.setResult(ACCEPT_LOSE);
        } else {
            deleted = killNode(enemyNode);
            newNode = addNewTower(fromNode, player,
                    moveUnits - delta, move);
            moveBroadcast.addNewNode(newNode);
            moveBroadcast.addNewLink(fromNode, newNode);
            moveBroadcast.addValueUpdate(fromNode);
            moveBroadcast.setRemovedNodes(deleted.getNodes());
            moveBroadcast.setRemovedLinks(deleted.getLinks());
            moveBroadcast.setResult(ACCEPT_WIN);
        }
        player.addToUnits(-delta);
        enemy.addToUnits(delta);

        return moveBroadcast;
    }

    /**
     * @param node Node to kill
     * @return Special container that consists of deleted nodes and links
     */
    public NodesAndLinks killNode(Node node) {
        if (node == null)
            return null;
        world[node.getY()][node.getX()] = null;
        if (node.isBonus()) {
            Vector<RNode> deleteNodes = new Vector<>();
            deleteNodes.add(node.getReduced());
            return new NodesAndLinks(deleteNodes, null);
        } else {
            Player player = room.getPlayer(node.getPid());
            HashMap<Node, HashSet<Node>> nodesMap = player.getNodesMap();

            // If killed main player's node
            // Remove player from game
            if (player.getMainNode() == node) {
                room.removePlayer(player);
                Vector<RNode> deleteNodes = player.getReducedNodes();
                deleteNodes.forEach(n -> {
                    setNodeToPosition(n.getX(), n.getY(), null);
                });
                HashSet<NodesLink> deletedLinks = new HashSet<>();
                nodesMap.forEach((n1, nodes) -> {
                    nodes.forEach(n2 -> deletedLinks.add(new NodesLink(n1.getReduced(), n2.getReduced())));
                });
                Vector<NodesLink> linksv = new Vector<>();
                linksv.addAll(deletedLinks);
                if (linksv.isEmpty())
                    linksv = null;
                return new NodesAndLinks(deleteNodes, linksv);
            }

            Vector<RNode> deleteNodes = new Vector<>();
            HashSet<NodesLink> deletedLinks = new HashSet<>();

            // Add THIS node to returning vectors
            deleteNodes.add(node.getReduced());
            nodesMap.get(node).forEach(n -> {
                deletedLinks.add(new NodesLink(node.getReduced(), n.getReduced()));
            });

            // Remove THIS node from map
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

            // Add to returning vectors not visited nodes and their links
            visitedNodes.forEach((n, visited) -> {
                if (!visited) {
                    for (Node v : nodesMap.get(n)) {
                        nodesMap.get(v).remove(n);
                        deletedLinks.add(new NodesLink(n.getReduced(), v.getReduced()));
                    }
                    nodesMap.remove(n);
                    deleteNodes.add(n.getReduced());
                    setNodeToPosition(n.getX(), n.getY(), null);
                }
            });

            // Return vector instead of set
            Vector<NodesLink> linksv = new Vector<>();
            linksv.addAll(deletedLinks);
            if (linksv.isEmpty())
                linksv = null;
            return new NodesAndLinks(deleteNodes, linksv);
        }
    }

    private class NodesAndLinks {
        Vector<RNode> nodes;
        Vector<NodesLink> links;

        public NodesAndLinks(Vector<RNode> nodes, Vector<NodesLink> links) {
            this.nodes = nodes;
            this.links = links;
        }

        public Vector<RNode> getNodes() {
            return nodes;
        }

        public Vector<NodesLink> getLinks() {
            return links;
        }
    }

    public void removeNode(@NotNull Node node) {
        setNodeToPosition(node.getX(), node.getY(), null);
    }

    @Nullable
    public MoveBroadcast acceptMove(Player player, Move move) {
        Node my = getByPosition(move.getXfrom(), move.getYfrom());
        Node target = getByPosition(move.getXto(), move.getYto());
        int moveRadius = (int) Math.sqrt((move.getXto() - move.getXfrom()) * (move.getXto() - move.getXfrom()) +
                        (move.getYto() - move.getYfrom()) * (move.getYto() - move.getYfrom()));
        MoveBroadcast moveBroadcast = null;

        if (my != target && checkTurn(my) && moveRadius <= room.getMoveRadius()) {
            if (target == null) {
                moveBroadcast = playerMoveFree(player, my, move);
            } else if (checkTurn(target)) {
                // Check if nodes are already linked
                if (player.getNodesMap().get(my).contains(target))
                    moveBroadcast = playerMoveUnitsMove(player, my, target, move);
                else
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
