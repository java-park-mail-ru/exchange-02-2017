package com.cyclic.models.game;

import com.cyclic.LOG;
import com.cyclic.models.game.net.fromclient.Move;
import com.cyclic.models.game.net.toclient.NodesLink;
import com.cyclic.models.game.net.toclient.RNode;
import com.cyclic.models.game.net.toclient.broadcast.MoveBroadcast;
import com.cyclic.models.game.net.toclient.broadcast.NewBonusBroadcast;
import com.sun.istack.internal.NotNull;
import com.sun.istack.internal.Nullable;

import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Stack;
import java.util.concurrent.ThreadLocalRandom;

import static com.cyclic.configs.Enums.MoveResult.*;

/**
 * Created by serych on 03.04.17.
 */
public class GameField {
    private transient int height, width;
    private transient Node[][] world;
    private transient ArrayList<Point> freePoints;
    private transient Room room;

    public GameField(Room room) {
        this.room = room;
        this.height = room.getFieldHeight();
        this.width = room.getFieldWidth();
        world = new Node[height][width];
        freePoints = new ArrayList<>();
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
            freePoints.add(new Point(x, y));
        } else {
            freePoints.remove(new Point(x, y));
        }
        world[y][x] = node;
    }

    public void addAndBroadcastRandomBonuses(int count) {
        ArrayList<Node> bonuses = new ArrayList<>();
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

    private Node addNewTower(@Nullable Node parentNode, Player player, int unitsCount, Move move) {
        int x = move.getXto();
        int y = move.getYto();
        Node node = new Node(
                player.getId(),
                unitsCount,
                x, y);
        player.getNodesMap().put(node, new HashSet<>());
        if (parentNode != null)
            linkNodes(node, parentNode, player);
        setNodeToPosition(x, y, node);
        return node;
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

    private void linkNodes(Node l, Node r, @Nullable Player player) {
        if (l == null || r == null) {
            throw new RuntimeException("You cannot link null nodes");
        }
        if (l.getPid() != r.getPid() || l.getPid() == -1) {
            throw new RuntimeException("You cannot link nodes of different players or bonuses");
        }
        if (player == null) {
            player = room.getPlayer(l.getPid());
        }
        HashMap<Node, HashSet<Node>> nodesMap = player.getNodesMap();
        nodesMap.get(l).add(r);
        nodesMap.get(r).add(l);
    }

    private void unlinkNodes(Node l, Node r, @Nullable Player player) {
        if (l == null || r == null) {
            return;
        }
        if (l.getPid() != r.getPid() || l.getPid() == -1) {
            return;
        }
        if (player == null) {
            player = room.getPlayer(l.getPid());
        }
        HashMap<Node, HashSet<Node>> nodesMap = player.getNodesMap();
        nodesMap.get(l).remove(r);
        nodesMap.get(r).remove(l);
    }

    private MoveBroadcast playerMoveFreeOrBonus(Player player, Node fromNode, Node bonus, Move move) {
        int moveUnits = move.getUnitsCount();
        if (!(moveUnits >= 1 && moveUnits < fromNode.getValue())) {
            return null;
        }
        fromNode.addToValue(-moveUnits);

        if (bonus != null) {
            player.addToUnits(bonus.getValue());
            moveUnits += bonus.getValue();
        }

        Node newNode = addNewTower(null, player,
                moveUnits, move);

        if (bonus != null) {
            addAndBroadcastRandomBonuses(1);
        }

        MoveBroadcast moveBroadcast = new MoveBroadcast();
        moveBroadcast.setResult(ACCEPT_OK);
        moveBroadcast.addNewNode(newNode);
        moveBroadcast.addValueUpdate(fromNode);

        Node left = null;
        Node right = null;
        Node top = null;
        Node bottom = null;
        try {
            left = getByPosition(newNode.getX() - 1, newNode.getY());
        } catch (ArrayIndexOutOfBoundsException ignored) {
        }
        try {
            right = getByPosition(newNode.getX() + 1, newNode.getY());
        } catch (ArrayIndexOutOfBoundsException ignored) {
        }
        try {
            top = getByPosition(newNode.getX(), newNode.getY() + 1);
        } catch (ArrayIndexOutOfBoundsException ignored) {
        }
        try {
            bottom = getByPosition(newNode.getX(), newNode.getY() - 1);
        } catch (ArrayIndexOutOfBoundsException ignored) {
        }

        ArrayList<Node> kostyl = new ArrayList<>(4);
        kostyl.add(left);
        kostyl.add(right);
        kostyl.add(top);
        kostyl.add(bottom);
        boolean standardMove = true;
        for (int i = 0; i < 4; i += 2) {
            Node a = kostyl.get(i);
            Node b = kostyl.get(i + 1);
            if (a != null &&
                    b != null &&
                    a.getPid() == b.getPid()) {
                if (a.getPid() == -1)
                    continue;
                if (a.getPid() == player.getId()) {
                    standardMove = false;
                    moveBroadcast.addRemovedLink(a, b);
                    unlinkNodes(a, b, player);
                    moveBroadcast.addNewLink(a, newNode);
                    linkNodes(a, newNode, player);
                    moveBroadcast.addNewLink(b, newNode);
                    linkNodes(b, newNode, player);
                } else {
                    // TODO Add this verification
//                    Player enemy = room.getPlayer(a.getPid());
//                    if (enemy.getNodesMap().get(a).contains(b)) {
//                        return null;
//                    }
                }
            }
        }
        if (standardMove) {
            moveBroadcast.addNewLink(fromNode, newNode);
            linkNodes(fromNode, newNode, player);
        }

        return moveBroadcast;
    }


    private MoveBroadcast playerMoveLink(Player player, Node fromNode, Node toNode, Move move) {
        int moveUnits = move.getUnitsCount();
        fromNode.addToValue(-moveUnits);
        toNode.addToValue(moveUnits);

        MoveBroadcast moveBroadcast = new MoveBroadcast();
        moveBroadcast.setResult(ACCEPT_OK);
        moveBroadcast.addNewLink(fromNode, toNode);
        linkNodes(fromNode, toNode, player);
        moveBroadcast.addValueUpdate(fromNode);
        moveBroadcast.addValueUpdate(toNode);

        return moveBroadcast;
    }

    private MoveBroadcast playerMoveAttack(Player player, Node fromNode, Node enemyNode, Move move) {
        int moveUnits = move.getUnitsCount();

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
            enemyNode.addToValue(-moveUnits + delta);
            moveBroadcast.addValueUpdate(enemyNode);
            setNodeToPosition(fromNode.getX(), fromNode.getY(), null);
            if (fromNode == player.getMainNode()) {
                moveBroadcast.setDeadpid(player.getId());
            } else {
                moveBroadcast.addRemovedNode(fromNode.getReduced());
                player.getNodesMap().get(fromNode).forEach(node -> {
                    moveBroadcast.addRemovedLink(fromNode, node);
                });
            }
            moveBroadcast.setResult(ACCEPT_LOSE);
        } else {
            //fromNode.addToValue(-moveUnits);
            //moveBroadcast.addValueUpdate(fromNode);
            deleted = killNode(enemyNode);
            move.setUnitsCount(moveUnits + delta);
            moveBroadcast.addOtherMoveBroadcast(playerMoveFreeOrBonus(player, fromNode, null, move));
            //newNode = addNewTower(fromNode, player,
            //        moveUnits + delta, move);
            //moveBroadcast.addNewNode(newNode);
            //moveBroadcast.addNewLink(fromNode, newNode);

            if (enemy.getMainNode() == enemyNode) {
                moveBroadcast.setDeadpid(enemy.getId());
            } else {
                moveBroadcast.setRemovedNodes(deleted.getNodes());
                moveBroadcast.setRemovedLinks(deleted.getLinks());
            }
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
            HashSet<RNode> deleteNodes = new HashSet<>();
            deleteNodes.add(node.getReduced());
            return new NodesAndLinks(deleteNodes, null);
        } else {
            Player player = room.getPlayer(node.getPid());
            HashMap<Node, HashSet<Node>> nodesMap = player.getNodesMap();

            // If killed main player's node
            // Remove player from game
            if (player.getMainNode() == node) {
                HashSet<RNode> deleteNodes = player.getReducedNodes();
                deleteNodes.forEach(n -> setNodeToPosition(n.getX(), n.getY(), null));
                HashSet<NodesLink> deletedLinks = new HashSet<>();
                nodesMap.forEach((n1, nodes) -> {
                    nodes.forEach(n2 -> deletedLinks.add(new NodesLink(n1.getReduced(), n2.getReduced())));
                });
                HashSet<NodesLink> linksv = new HashSet<>();
                linksv.addAll(deletedLinks);
                if (linksv.isEmpty())
                    linksv = null;
                return new NodesAndLinks(deleteNodes, linksv);
            }

            HashSet<RNode> deleteNodes = new HashSet<>();
            HashSet<NodesLink> deletedLinks = new HashSet<>();

            // Add THIS node to returning HashSets
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
                setNodeToPosition(node.getX(), node.getY(), null);
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

            // Add to returning HashSets not visited nodes and their links
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

            // Return HashSet instead of set
            HashSet<NodesLink> linksv = new HashSet<>();
            linksv.addAll(deletedLinks);
            if (linksv.isEmpty())
                linksv = null;
            return new NodesAndLinks(deleteNodes, linksv);
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
                moveBroadcast = playerMoveFreeOrBonus(player, my, null, move);
            } else if (checkTurn(target)) {
                // Check if nodes are already linked
                if (player.getNodesMap().get(my).contains(target))
                    moveBroadcast = playerMoveUnitsMove(player, my, target, move);
                else
                    moveBroadcast = playerMoveLink(player, my, target, move);
            } else {
                if (target.isBonus())
                    moveBroadcast = playerMoveFreeOrBonus(player, my, target, move);
                else
                    moveBroadcast = playerMoveAttack(player, my, target, move);
            }
        }
        return moveBroadcast;
    }

    private class NodesAndLinks {
        HashSet<RNode> nodes;
        HashSet<NodesLink> links;

        public NodesAndLinks(HashSet<RNode> nodes, HashSet<NodesLink> links) {
            this.nodes = nodes;
            this.links = links;
        }

        public HashSet<RNode> getNodes() {
            return nodes;
        }

        public HashSet<NodesLink> getLinks() {
            return links;
        }
    }
}
