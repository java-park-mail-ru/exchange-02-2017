package com.cyclic.models.game;

import java.util.Vector;

/**
 * Created by serych on 03.04.17.
 */
public class Node {
    private Node parentNode;
    private Vector<Node> nextNodes;
    private int playerID;
    private int type;
    private int x;
    private int y;

    public Node(Node parentNode, int playerID, int type, int x, int y) {
        this.parentNode = parentNode;
        this.nextNodes = new Vector<>();
        this.playerID = playerID;
        this.type = type;
        this.x = x;
        this.y = y;
    }

    public void addChild(Node node) {
        nextNodes.add(node);
    }

    public Node getParentNode() {
        return parentNode;
    }

    public void setParentNode(Node parentNode) {
        this.parentNode = parentNode;
    }

    public Vector<Node> getNextNodes() {
        return nextNodes;
    }

    public void setNextNodes(Vector<Node> nextNodes) {
        this.nextNodes = nextNodes;
    }

    public int getPlayerID() {
        return playerID;
    }

    public void setPlayerID(int playerID) {
        this.playerID = playerID;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
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
