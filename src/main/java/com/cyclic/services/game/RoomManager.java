package com.cyclic.services.game;

import com.cyclic.models.game.Room;
import org.springframework.web.socket.WebSocketSession;


import java.util.Vector;

/**
 * Created by serych on 02.04.17.
 */
public class RoomManager {
    Vector<Room> rooms = new Vector<>();

    public void findRoomForThisGuy(WebSocketSession session) {
        for (Room room:rooms) {
            if (room.addSession(session))
                return;
        }
        Room newRoom = new Room(rooms.size());
        newRoom.addSession(session);
        rooms.add(newRoom);
    }
}
