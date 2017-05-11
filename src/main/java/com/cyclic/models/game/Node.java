package com.cyclic.models.game;

import java.util.Vector;

/**
 * Created by serych on 03.04.17.
 */
public class Node {

    private Vector<Node> linkedNodes;
    private long playerID;
    private int value;
    private int type;
    private int x;
    private int y;

    public Node(Node parentNode, long playerID, int value, int type, int x, int y) {
        this.linkedNodes = new Vector<>();
        linkedNodes.add(parentNode);
        this.value = value;
        this.playerID = playerID;
        this.type = type;
        this.x = x;
        this.y = y;
    }

    public void addChild(Node node) {
        linkedNodes.add(node);
    }

    public Vector<Node> getLinkedNodes() {
        return linkedNodes;
    }

    public void setLinkedNodes(Vector<Node> linkedNodes) {
        this.linkedNodes = linkedNodes;
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
