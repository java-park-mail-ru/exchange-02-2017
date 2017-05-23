package com.cyclic.services.game;

import com.cyclic.Application;
import com.cyclic.configs.ResourceManager;
import com.cyclic.configs.RoomConfig;
import com.cyclic.models.game.Player;
import com.cyclic.models.game.Room;
import com.cyclic.models.game.net.toclient.broadcast.RoomManagerUpdateBroadcast;
import com.cyclic.services.AccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Vector;
import java.util.concurrent.ConcurrentHashMap;

import static com.cyclic.configs.Enums.RoomStatus.STATUS_CREATING;

/**
 * Created by serych on 02.04.17.
 */
@Service
public class RoomManager {
    public static ResourceManager resourceManager;
    public static AccountService accountService;
    private static RoomConfig mainRoomConfig;
    private ConcurrentHashMap<Player, Room> allRooms;
    private Vector<Player> playersWithNoRoom;
    private Room[] freeRooms;
    private long lastRoomId = 0;
    private String json = "";
    RoomManagerUpdateBroadcast broadcast;

    @Autowired
    public RoomManager(ResourceManager resourceManager, AccountService accountService) {
        RoomManager.resourceManager = resourceManager;
        RoomManager.accountService = accountService;
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
                if (((double) freeRooms[i].getPlayersCount()) / freeRooms[i].getCapacity() > koef) {
                    roomCapacity = i + 2;
                    koef = ((double) freeRooms[i].getPlayersCount()) / freeRooms[i].getCapacity();
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
        room.removePlayer(player, false);
        if (room.getStatus() == STATUS_CREATING) {
            broadcast.getRoomData(room.getCapacity()).setQueue(room.getPlayersCount());
            json = Application.gson.toJson(broadcast);
            playersWithNoRoom.forEach(p -> {
                p.sendString(json);
            });
        }
    }

    public void addPlayerWithNoRoom(Player player) {
        playersWithNoRoom.add(player);
        player.sendString(json);
    }
}
