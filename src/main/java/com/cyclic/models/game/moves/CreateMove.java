package com.cyclic.models.game.moves;

import com.google.gson.annotations.Expose;

/**
 * Created by serych on 03.04.17.
 */
public class CreateMove {
    private Integer xfrom;
    private Integer yfrom;
    private Integer xto;
    private Integer yto;
    private Integer unitsCount;
    @Expose
    private Integer parentUnitsCount;

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

    public Integer getParentUnitsCount() {
        return parentUnitsCount;
    }

    public void setParentUnitsCount(int parentUnitsCount) {
        this.parentUnitsCount = parentUnitsCount;
    }
}
