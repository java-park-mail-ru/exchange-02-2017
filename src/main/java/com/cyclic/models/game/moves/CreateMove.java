package com.cyclic.models.game.moves;

/**
 * Created by serych on 03.04.17.
 */
public class CreateMove {
    private Integer xfrom;
    private Integer yfrom;
    private Integer xto;
    private Integer yto;
    private Integer unitsCount;
    private int parentUnitsCount;

    public Integer getXfrom() {
        return xfrom;
    }

    public Integer getYfrom() {
        return yfrom;
    }

    public Integer getXto() {
        return xto;
    }

    public Integer getYto() {
        return yto;
    }

    public Integer getUnitsCount() {
        return unitsCount;
    }

    public int getParentUnitsCount() {
        return parentUnitsCount;
    }

    public void setParentUnitsCount(int parentUnitsCount) {
        this.parentUnitsCount = parentUnitsCount;
    }
}
