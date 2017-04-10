package com.cyclic.models.game;

import com.cyclic.models.game.net.PlayerMoveBroadcast;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.Vector;
import java.util.concurrent.ThreadLocalRandom;

import static com.cyclic.controllers.WebSocketController.DATATYPE_ROOMINFO;
import static com.cyclic.models.game.net.PlayerMoveBroadcast.MOVE_NORM;
import static com.cyclic.models.game.net.PlayerMoveBroadcast.MOVE_TEMP;

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
    public static final int FIELD_WIDTH  = 20;
    public static final int FIELD_HEIGHT = 20;

    private final int datatype = DATATYPE_ROOMINFO;
    private Vector<Player> players;
    private Long pid;
    private int status;
    private long roomID;

    private transient int performingId;
    private transient Gson gson;
    private transient GameField field;

    public Room(long roomID) {
        status = STATUS_CREATING;
        players = new Vector<>(4);
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

    public void removePlayer(Player player) {
        if (players.remove(player))
            stop();
    }

    private void broadcastRoomUpdate() {
        for (Player player: players) {
            player.sendString(gson.toJson(this));
        }
    }

    public void broadcast(String data) {
        for (Player player: players) {
            player.sendString(data);
        }
    }

    public boolean start() {
        if (status == STATUS_READY) {
            for (Player player: players) {
                if (!player.isReadyForGameStart()) {
                    broadcastRoomUpdate();
                    return false;
                }
            }
            status = STATUS_PLAYING;
            performingId = ThreadLocalRandom.current().nextInt(0, Room.PLAYERS_COUNT);
            pid = players.get(performingId).getId();
            broadcastRoomUpdate();
            field = new GameField(this, FIELD_HEIGHT, FIELD_WIDTH);
            for (Player player: players) {
                Node node =  new Node(null,
                        player.getId(),
                        START_TOWER_UNITS,
                        Node.NODE_TOWER,
                        player.getBeginX(),
                        player.getBeginY());
                field.setNodeToPosition(player.getBeginX(), player.getBeginY(), node);
            }
            for (int i = 0; i < START_BONUS_COUNT; i++) {
                field.addRandomBonus();
            }
            return true;
        }
        broadcastRoomUpdate();
        return false;
    }

    public int getStatus() {
        return status;
    }

    public void handlePlayersMove(Player player, Moves moves) {
        if (status == STATUS_PLAYING && field != null && pid.equals(player.getId())) {
            field.setPossibleMoves(moves);
            broadcast(gson.toJson(new PlayerMoveBroadcast(player.getId(), MOVE_TEMP, moves)));
        }
        else {
            player.disconnectBadApi("Cannot handle move, or room is not playing");
        }
    }

    public void acceptMove(Player player) {
        if (status == STATUS_PLAYING && field != null && pid.equals(player.getId())) {
            if (!field.acceptMove()) {
                player.disconnectBadApi("Your are moving like an asshole");
                return;
            }
            broadcast(gson.toJson(new PlayerMoveBroadcast(player.getId(), MOVE_NORM, field.getPossibleMoves())));
            performingId += 1;
            performingId %= PLAYERS_COUNT;
            pid = players.get(performingId).getId();
            broadcastRoomUpdate();
        }
        else {
            player.disconnectBadApi("Cannot accept. It is not your move.");
        }
    }

    public int getPerformingId() {
        return performingId;
    }

    public Long getPid() {
        return pid;
    }

    public Gson getGson() {
        return gson;
    }

    public void stop() {
        status = STATUS_CREATING;
        if (players.size() == PLAYERS_COUNT) {
            status = STATUS_READY;
        }
        if (players.size() == 1) {

        }
        field = null;
        pid = null;

        broadcastRoomUpdate();
    }
}
