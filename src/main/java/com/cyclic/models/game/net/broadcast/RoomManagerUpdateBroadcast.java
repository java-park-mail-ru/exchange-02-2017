package com.cyclic.models.game.net.broadcast;

import com.cyclic.configs.Enums;

import java.awt.*;
import java.util.Vector;

/**
 * Created by serych on 17.05.17.
 */
public class RoomManagerUpdateBroadcast {
    Data[] freerooms;
    private transient int roomsCount;

    public RoomManagerUpdateBroadcast(int roomsCount) {
        this.roomsCount = roomsCount;
        freerooms = new Data[roomsCount];
        for (int i = 0; i < roomsCount; i++) {
            freerooms[i] = new Data(i + 2, 0);
        }
    }

    public Data getRoomData(int playersCount) {
        if (playersCount < 2 || playersCount > roomsCount + 1) {
            return null;
        }
        return freerooms[playersCount - 2];
    }

    private final Enums.Datatype datatype = Enums.Datatype.DATATYPE_ROOMMANAGER_UPDATE;

    public static class Data {
        int capactiy;
        int queue;

        public Data(int capactiy, int queue) {
            this.capactiy = capactiy;
            this.queue = queue;
        }

        public int getCapactiy() {
            return capactiy;
        }

        public void setCapactiy(int capactiy) {
            this.capactiy = capactiy;
        }

        public int getQueue() {
            return queue;
        }

        public void setQueue(int queue) {
            this.queue = queue;
        }
    }
}
