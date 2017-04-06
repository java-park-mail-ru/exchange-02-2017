package com.cyclic.models.game.net;

import static com.cyclic.controllers.WebSocketController.DATATYPE_NEWBONUS;
import static com.cyclic.controllers.WebSocketController.DATATYPE_ROOMINFO;

/**
 * Created by serych on 05.04.17.
 */
public class NewBonusBroadcast {
    private final int datatype = DATATYPE_NEWBONUS;
    private int x;
    private int y;
    private int value;

    public NewBonusBroadcast(int x, int y, int value) {
        this.x = x;
        this.y = y;
        this.value = value;
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
}
