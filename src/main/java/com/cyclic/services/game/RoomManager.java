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

/**
 * Created by serych on 02.04.17.
 */
@Service
public class RoomManager {
    public static ResourceManager resourceManager;
    public static AccountService accountService;
    private static RoomConfig mainRoomConfig;
    private Vector<Player> playersWithNoRoom;
    private Room[] freeRooms;
    private String json = "";
    RoomManagerUpdateBroadcast broadcast;

    @Autowired
    public RoomManager(ResourceManager resourceManager, AccountService accountService) {
        RoomManager.resourceManager = resourceManager;
        RoomManager.accountService = accountService;
        mainRoomConfig = resourceManager.getRoomConfig();
        int basewidth = mainRoomConfig.getFieldWidth();
        int baseheight = mainRoomConfig.getFieldHeight();
        playersWithNoRoom = new Vector<>();
        freeRooms = new Room[mainRoomConfig.getPlayersCount() - 1];
        broadcast = new RoomManagerUpdateBroadcast(mainRoomConfig.getPlayersCount() - 1);
        for (int i = 2; i <= resourceManager.getRoomConfig().getPlayersCount(); i++) {
            RoomConfig config = new RoomConfig(resourceManager.getRoomConfig());
            config.setPlayersCount(i);
            config.setFieldWidth(basewidth);
            config.setFieldHeight(baseheight);
            baseheight *= 1.5;
            basewidth *= 1.5;
            config.setStartBonusCount((int) (config.getFieldWidth() * config.getFieldHeight() * 0.05));
            freeRooms[i - 2] = new Room(this, config);
        }
        json = Application.gson.toJson(broadcast);
    }

    /**
     * Very important method! Finds a room for a player and starts room if it is full
     *
     * @param player       Player to find a room
     * @param roomCapacity Desired capacity of the room. Unnecessary param.
     */
    public synchronized void findRoomForThisGuy(Player player, Integer roomCapacity) {
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

        playersWithNoRoom.remove(player);

        if (room.isAlmostFull()) {
            freeRooms[roomCapacity - 2] = new Room(this, room.getRoomConfig());
            broadcast.getRoomData(roomCapacity).setQueue(0);
        } else
            broadcast.getRoomData(roomCapacity).setQueue(room.getPlayersCount() + 1);
        updateAndBroadcastJson();

        // !!!!!!
        room.addPlayer(player);
        // !!!!!!
    }

    public void updateAndBroadcastJson() {
        json = Application.gson.toJson(broadcast);
        playersWithNoRoom.forEach(p -> {
            p.sendString(json);
        });
    }

    public RoomManagerUpdateBroadcast getBroadcast() {
        return broadcast;
    }

    public void addPlayerWithNoRoom(Player player) {
        playersWithNoRoom.add(player);
        player.sendString(json);
    }

    public void removePlayerWithNoRoom(Player player) {
        playersWithNoRoom.remove(player);
    }
}
