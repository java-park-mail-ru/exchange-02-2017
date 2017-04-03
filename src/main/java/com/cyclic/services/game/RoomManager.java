package com.cyclic.services.game;

import com.cyclic.models.game.Player;
import com.cyclic.models.game.Room;
import org.springframework.web.socket.WebSocketSession;


import java.util.Vector;

/**
 * Created by serych on 02.04.17.
 */
public class RoomManager {
    Vector<Room> rooms = new Vector<>();

    public void findRoomForThisGuy(Player player) {
        for (Room room:rooms) {
            if (room.addPlayer(player))
                return;
        }
        Room newRoom = new Room(rooms.size());
        newRoom.addPlayer(player);
        rooms.add(newRoom);
    }

    public void deletePlayerFromAnyRoom(Player player) {
        for (Room room:rooms) {
            if (room.deletePlayer(player))
                return;
        }
    }
}
