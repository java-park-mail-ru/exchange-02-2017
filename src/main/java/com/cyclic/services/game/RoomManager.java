package com.cyclic.services.game;

import com.cyclic.Application;
import com.cyclic.configs.ResourceManager;
import com.cyclic.models.game.Player;
import com.cyclic.models.game.Room;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Vector;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by serych on 02.04.17.
 */
@Service
public class RoomManager {
    private ConcurrentHashMap<Player, Room> allRooms;
    private Vector<Room> freeRooms;
    private long lastRoomId = 0;

    private ResourceManager resourceManager;

    public RoomManager() {
        this.resourceManager = Application.resourceManager;
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
            Room newRoom = new Room(lastRoomId, resourceManager.getRoomConfig());
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
        Player lastPlayer = room.removePlayer(player);
        if (lastPlayer != null) {
            lastPlayer.setRoom(null);
            lastPlayer.setReadyForGameStart(false);
            //findRoomForThisGuy(lastPlayer);
        }
    }
}
