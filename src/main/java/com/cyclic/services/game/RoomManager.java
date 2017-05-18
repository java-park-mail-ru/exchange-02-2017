package com.cyclic.services.game;

import com.cyclic.Application;
import com.cyclic.configs.ResourceManager;
import com.cyclic.configs.RoomConfig;
import com.cyclic.models.game.Player;
import com.cyclic.models.game.Room;
import com.cyclic.models.game.net.broadcast.RoomManagerUpdateBroadcast;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.awt.*;
import java.util.Vector;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by serych on 02.04.17.
 */
@Service
public class RoomManager {
    public static ResourceManager resourceManager;
    private static RoomConfig mainRoomConfig;
    private ConcurrentHashMap<Player, Room> allRooms;
    private Vector<Player> playersWithNoRoom;
    private Room[] freeRooms;
    private long lastRoomId = 0;
    private String json = "";
    RoomManagerUpdateBroadcast broadcast;

    @Autowired
    public RoomManager(ResourceManager resourceManager) {
        RoomManager.resourceManager = resourceManager;
        mainRoomConfig = resourceManager.getRoomConfig();
        allRooms = new ConcurrentHashMap<>();
        playersWithNoRoom = new Vector<>();
        freeRooms = new Room[mainRoomConfig.getPlayersCount() - 1];
        broadcast = new RoomManagerUpdateBroadcast(mainRoomConfig.getPlayersCount() - 1);
        for (int i = 2; i <= resourceManager.getRoomConfig().getPlayersCount(); i++) {
            RoomConfig config = new RoomConfig(resourceManager.getRoomConfig());
            config.setPlayersCount(i);
            freeRooms[i - 2] = new Room(lastRoomId, this, config);
            lastRoomId++;
        }
        json = Application.gson.toJson(broadcast);
    }

    public Room getPlayersRoom(Player player) {
        return allRooms.get(player);
    }

    /**
     * Very important method! Finds a room for a player and starts room if it is full
     * @param player Player to find a room
     * @param roomCapacity Desired capacity of the room. Unnecessary param.
     */
    public synchronized void findRoomForThisGuy(Player player, Integer roomCapacity) {
        if (getPlayersRoom(player) != null) {
            player.disconnectBadApi("You already have a room");
            return;
        }
        if (roomCapacity == null) {
            double koef = 0;
            roomCapacity = 2;
            for (int i = mainRoomConfig.getPlayersCount() - 2; i > 0; i--) {
                if (freeRooms[i].getPlayersCount() / freeRooms[i].getCapacity() > koef) {
                    roomCapacity = i;
                    koef = freeRooms[i].getPlayersCount() / freeRooms[i].getCapacity();
                }
            }
        }
        if (roomCapacity < 2 || roomCapacity > mainRoomConfig.getPlayersCount()) {
            player.disconnectBadApi("Bad room capacity");
            return;
        }
        Room room = freeRooms[roomCapacity - 2];

        // !!!!!!
        room.addPlayer(player);
        // !!!!!!


        allRooms.put(player, room);
        playersWithNoRoom.remove(player);
        if (room.isFull()) {
            freeRooms[roomCapacity - 2] = new Room(lastRoomId, this, room.getRoomConfig());
            broadcast.getRoomData(roomCapacity).setQueue(0);
        }
        else
            broadcast.getRoomData(roomCapacity).setQueue(room.getPlayersCount());
        json = Application.gson.toJson(broadcast);
        playersWithNoRoom.forEach(p -> {
            p.sendString(json);
        });
    }

    public void deletePlayerFromAnyRoom(Player player, boolean keepOnServer) {
        Room room = allRooms.remove(player);
        if (room == null) {
            if (keepOnServer) {
                player.disconnectBadApi("You cannot leave room until you haven entered one");
            }
            playersWithNoRoom.remove(player);
            return;
        }
        if (keepOnServer) {
            player.setRoom(null);
            addPlayerWithNoRoom(player);
        }
        Player lastPlayer = room.removePlayer(player);
        if (lastPlayer != null) {
            lastPlayer.setRoom(null);
            addPlayerWithNoRoom(lastPlayer);
        }
    }

    public void addPlayerWithNoRoom(Player player) {
        playersWithNoRoom.add(player);
        player.sendString(json);
    }
}
