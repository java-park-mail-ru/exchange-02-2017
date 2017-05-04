package com.cyclic.models.game;


/**
 * Created by serych on 03.04.17.
 */
public class Move {
    private Integer type;
    private Integer xfrom;
    private Integer yfrom;
    private Integer xto;
    private Integer yto;
    private Integer unitsCount;
    private Integer parentUnitsCount;

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
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
}
