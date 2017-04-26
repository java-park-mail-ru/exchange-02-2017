package com.cyclic.models.game;

import com.cyclic.models.game.net.PlayerDisconnectBroadcast;
import com.cyclic.models.game.net.PlayerMoveBroadcast;
import com.cyclic.models.game.net.RoomDestructionBroadcast;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.awt.*;
import java.util.Vector;
import java.util.concurrent.ThreadLocalRandom;

import static com.cyclic.configs.Constants.DATATYPE_ROOMINFO;
import static com.cyclic.configs.Constants.NODE_TOWER;

/**
 * Created by serych on 31.03.17.
 */
public class Room {
    public static final int STATUS_CREATING = 0;
    public static final int STATUS_PLAYING = 1;
    public static final int STATUS_READY = 2;

    public static final int PLAYERS_COUNT = 2;
    public static final int START_BONUS_COUNT = 10;
    public static final int START_TOWER_UNITS = 100;
    public static final int BONUS_MIN_VALUE = 10;
    public static final int BONUS_MAX_VALUE = 100;
    public static final int FIELD_WIDTH = 20;
    public static final int FIELD_HEIGHT = 20;

    private final int datatype = DATATYPE_ROOMINFO;
    private Vector<Player> players;
    private Long pid;
    private int status;
    private long roomID;

    private transient int performingPlayerIndex;
    private transient Gson gson;
    private transient GameField field;

    public Room(long roomID) {
        status = STATUS_CREATING;
        players = new Vector<>(PLAYERS_COUNT);
        field = null;
        gson = new GsonBuilder().create();
        this.roomID = roomID;
        pid = null;
    }

    public int getPlayersCount() {
        return players.size();
    }

    public boolean isEmpty() {
        return players.isEmpty();
    }

    public boolean isFull() {
        return players.size() == PLAYERS_COUNT;
    }

    /**
     * Adds player to this room
     *
     * @param player Player to add
     * @return True if the player was added. False if error has occurred
     */
    public boolean addPlayer(Player player) {
        if (status != STATUS_CREATING)
            return false;
        // TODO: Link webSocket session and HTTP session and give normal nickname
        player.setRoom(this);
        players.add(player);
        if (players.size() == PLAYERS_COUNT) {
            status = STATUS_READY;
        }
        broadcastRoomUpdate();
        return true;
    }

    /**
     * @param player Player to remove from this room
     * @return If the last player in this room, give him another room
     */
    public Player removePlayer(Player player) {
        if (players.remove(player)) {
            if (getPlayersCount() > 1) {
                broadcast(gson.toJson(new PlayerDisconnectBroadcast(player.getId())));
                if (status == STATUS_PLAYING) {
                    performingPlayerIndex %= PLAYERS_COUNT;
                    pid = players.get(performingPlayerIndex).getId();
                }
                broadcastRoomUpdate();
                return null;
            } else {
                broadcast(gson.toJson(new RoomDestructionBroadcast()));
                if (getPlayersCount() == 1)
                    return players.get(0);
            }
        }
        return null;
    }

    /**
     * Broadcasts this room info to all players in this room
     */
    private void broadcastRoomUpdate() {
        for (Player player : players) {
            player.sendString(gson.toJson(this));
        }
    }

    /**
     * Broadcasts data to all players in this room
     *
     * @param data Data to broadcast
     */
    public void broadcast(String data) {
        for (Player player : players) {
            player.sendString(data);
        }
    }

    /**
     * Start room!
     *
     * @return True if room started. False if error has occurred
     */
    public boolean start() {
        if (status == STATUS_READY) {
            for (Player player : players) {
                if (!player.isReadyForGameStart()) {
                    broadcastRoomUpdate();
                    return false;
                }
            }
            status = STATUS_PLAYING;
            performingPlayerIndex = ThreadLocalRandom.current().nextInt(0, Room.PLAYERS_COUNT);
            pid = players.get(performingPlayerIndex).getId();
            broadcastRoomUpdate();
            field = new GameField(this, FIELD_HEIGHT, FIELD_WIDTH);
            for (Player player : players) {
                Point point = field.findRandomNullPoint();

                player.setBeginX(point.x);
                player.setBeginY(point.y);
            }
            for (Player player : players) {
                Node node = new Node(null,
                        player.getId(),
                        START_TOWER_UNITS,
                        NODE_TOWER,
                        player.getBeginX(),
                        player.getBeginY());
                field.setNodeToPosition(player.getBeginX(), player.getBeginY(), node);
            }
            field.addAndBroadcastRandomBonuses(START_BONUS_COUNT);
            return true;
        }
        broadcastRoomUpdate();
        return false;
    }

    public int getStatus() {
        return status;
    }

    public void acceptMove(Player player, Move move) {
        if (status == STATUS_PLAYING && field != null && pid.equals(player.getId())) {
            if (!field.acceptMove(player, move)) {
                player.disconnectBadApi("Your are moving like an asshole");
                return;
            }
            long lastPid = pid;
            performingPlayerIndex += 1;
            performingPlayerIndex %= PLAYERS_COUNT;
            pid = players.get(performingPlayerIndex).getId();
            broadcast(gson.toJson(new PlayerMoveBroadcast(lastPid, pid, move)));
        } else {
            player.disconnectBadApi("Cannot accept. It is not your move.");
        }
    }

    public Long getPid() {
        return pid;
    }

    public Gson getGson() {
        return gson;
    }
}
