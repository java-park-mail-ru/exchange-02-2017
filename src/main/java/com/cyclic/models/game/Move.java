package com.cyclic.models.game;


import java.util.Vector;

/**
 * Created by serych on 03.04.17.
 */
public class Move {
    private MoveType type;
    private Integer xfrom;
    private Integer yfrom;
    private Integer xto;
    private Integer yto;
    private Integer unitsCount;
    private Integer parentUnitsCount;
    private Vector<Node> deletedNodes;

    public Vector<Node> getDeletedNodes() {
        return deletedNodes;
    }

    public void setDeletedNodes(Vector<Node> deletedNodes) {
        this.deletedNodes = deletedNodes;
    }

    public MoveType getType() {
        return type;
    }

    public void setType(MoveType type) {
        this.type = type;
    }

    public Integer getXfrom() {
        return xfrom;
    }

    public void setXfrom(Integer xfrom) {
        this.xfrom = xfrom;
    }

    public Integer getYfrom() {
        return yfrom;
    }

    public void setYfrom(Integer yfrom) {
        this.yfrom = yfrom;
    }

    public Integer getXto() {
        return xto;
    }

    public void setXto(Integer xto) {
        this.xto = xto;
    }

    public Integer getYto() {
        return yto;
    }

    public void setYto(Integer yto) {
        this.yto = yto;
    }

    public Integer getUnitsCount() {
        return unitsCount;
    }

    public void setUnitsCount(Integer unitsCount) {
        this.unitsCount = unitsCount;
    }

    public Integer getParentUnitsCount() {
        return parentUnitsCount;
    }

    public void setParentUnitsCount(Integer parentUnitsCount) {
        this.parentUnitsCount = parentUnitsCount;
    }

    public static enum MoveType {
        ACCEPT_OK,
        ACCEPT_WIN,
        ACCEPT_LOST,
        ACCEPT_FAIL
    }
}
