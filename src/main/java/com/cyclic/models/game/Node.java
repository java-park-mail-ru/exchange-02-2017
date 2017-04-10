package com.cyclic.models.game;

import java.util.Vector;

/**
 * Created by serych on 03.04.17.
 */
public class Node {

    public static final int NODE_TOWER = 0;
    public static final int NODE_BONUS = 1;

    private Node parentNode;
    private Vector<Node> nextNodes;
    private long playerID;
    private int value;
    private int type;
    private int x;
    private int y;

    public Node(Node parentNode, long playerID, int value, int type, int x, int y) {
        this.parentNode = parentNode;
        this.value = value;
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

    public long getPlayerID() {
        return playerID;
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

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }

    public void addToValue(int inc) {
        this.value += inc;
    }
}
