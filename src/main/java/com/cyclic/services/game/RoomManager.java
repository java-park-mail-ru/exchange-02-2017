package com.cyclic.services.game;

import com.cyclic.models.game.Player;
import com.cyclic.models.game.Room;


import java.util.HashSet;
import java.util.TreeSet;
import java.util.Vector;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by serych on 02.04.17.
 */
public class RoomManager {
    private ConcurrentHashMap<Player, Room> allRooms;
    private Vector<Room> freeRooms;
    private long lastRoomId = 0;

    public RoomManager() {
        allRooms = new ConcurrentHashMap<>();
        freeRooms = new Vector<>();
    }

    public Room getPlayersRoom(Player player) {
        return allRooms.get(player);
    }

    public void findRoomForThisGuy(Player player) {
        if (getPlayersRoom(player) != null) {
            player.disconnectBadApi("You already have a room");
            return;
        }
        if (freeRooms.isEmpty()) {
            Room newRoom = new Room(lastRoomId);
            lastRoomId++;
            freeRooms.add(newRoom);
        }
        Room room = freeRooms.get(0);
        room.addPlayer(player);
        allRooms.put(player, room);
        if (room.isFull()) {
            freeRooms.remove(room);
        }
    }

    public void deletePlayerFromAnyRoom(Player player) {
        Room room = allRooms.remove(player);
        if (room == null)
            return;
        room.removePlayer(player);
        room.stop();
        if (!room.isEmpty())
            freeRooms.add(room);
    }


}
