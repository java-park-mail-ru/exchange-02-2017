package com.cyclic.services.game;

import com.cyclic.models.game.Player;

import java.util.ArrayList;
import java.util.SortedSet;

/**
 * Created by serych on 02.06.17.
 */
public class GuestManager {
    public static final String guestPrefix = "Guest";
    private long nextid;
    private ArrayList<Long> freedPositions;

    public GuestManager() {
        this.freedPositions = new ArrayList<>();
        nextid = 0;
    }

    public String getNewGuestNick() {
        long id = 0;
        if (freedPositions.size() != 0) {
            id = freedPositions.remove(0);
        }
        else {
            id = nextid;
            nextid++;
        }
        return new StringBuilder().append(guestPrefix).append(id).toString();
    }

    public void freeGuestNick(String nick) {
        nick = nick.replace(guestPrefix, "");
        long id = Long.parseLong(nick);
        freedPositions.add(id);
    }
}
