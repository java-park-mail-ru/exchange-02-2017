package com.cyclic.models.game;

import com.cyclic.configs.Constants;
import com.cyclic.configs.RoomConfig;
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
    private final int datatype = DATATYPE_ROOMINFO;

    private transient int playersCount;
    private transient int startBonusCount;
    private transient int startTowerUnits;
    private transient int bonusMinValue;
    private transient int bonusMaxValue;
    private transient int fieldWidth;
    private transient int fieldHeight;
    private Vector<Player> players;
    private Long pid;
    private int status;
    private long roomID;

    private transient int performingPlayerIndex;
    private transient Gson gson;
    private transient GameField field;


    public Room(long roomID, RoomConfig roomConfig) {
        this.roomID = roomID;

        playersCount = roomConfig.getPlayersCount();
        startBonusCount = roomConfig.getStartBonusCount();
        startTowerUnits = roomConfig.getStartTowerUnits();
        bonusMaxValue = roomConfig.getBonusMaxValue();
        bonusMinValue = roomConfig.getBonusMinValue();
        fieldHeight = roomConfig.getFieldHeight();
        fieldWidth = roomConfig.getFieldWidth();

        status = Constants.STATUS_CREATING;
        players = new Vector<>(playersCount);
        field = new GameField(this, fieldHeight, fieldWidth);
        gson = new GsonBuilder().create();
        pid = null;
    }

    public int getPlayersCount() {
        return players.size();
    }

    public boolean isEmpty() {
        return players.isEmpty();
    }

    public boolean isFull() {
        return players.size() == playersCount;
    }

    /**
     * Adds player to this room
     *
     * @param player Player to add
     * @return True if the player was added. False if error has occurred
     */
    public boolean addPlayer(Player player) {
        if (status != Constants.STATUS_CREATING)
            return false;
        player.setRoom(this);
        players.add(player);
        Point point = field.findRandomNullPoint();
        player.setBeginX(point.x);
        player.setBeginY(point.y);
        field.setNodeToPosition(point.x,point.y, new Node(null, player.getId(), startTowerUnits, NODE_TOWER, point.x,point.y));
        if (players.size() == playersCount) {
            status = Constants.STATUS_FULL_SMB_NOT_READY;
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
                if (status == Constants.STATUS_PLAYING) {
                    performingPlayerIndex %= playersCount;
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
        if (status == Constants.STATUS_FULL_SMB_NOT_READY) {
            for (Player player : players) {
                if (!player.isReadyForGameStart()) {
                    broadcastRoomUpdate();
                    return false;
                }
            }
            status = Constants.STATUS_PLAYING;
            performingPlayerIndex = ThreadLocalRandom.current().nextInt(0, playersCount);
            pid = players.get(performingPlayerIndex).getId();
            broadcastRoomUpdate();
            for (Player player : players) {
                Node node = new Node(null,
                        player.getId(),
                        startTowerUnits,
                        NODE_TOWER,
                        player.getBeginX(),
                        player.getBeginY());
                field.setNodeToPosition(player.getBeginX(), player.getBeginY(), node);
            }
            field.addAndBroadcastRandomBonuses(startBonusCount);
            return true;
        }
        broadcastRoomUpdate();
        return false;
    }

    public int getStatus() {
        return status;
    }

    public void acceptMove(Player player, Move move) {
        if (status == Constants.STATUS_PLAYING && field != null && pid.equals(player.getId())) {
            if (!field.acceptMove(player, move)) {
                player.disconnectBadApi("Your are moving like an asshole");
                return;
            }
            long lastPid = pid;
            performingPlayerIndex += 1;
            performingPlayerIndex %= playersCount;
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

    public int getStartBonusCount() {
        return startBonusCount;
    }

    public int getStartTowerUnits() {
        return startTowerUnits;
    }

    public int getBonusMinValue() {
        return bonusMinValue;
    }

    public int getBonusMaxValue() {
        return bonusMaxValue;
    }

    public int getFieldWidth() {
        return fieldWidth;
    }

    public int getFieldHeight() {
        return fieldHeight;
    }

    public long getRoomID() {
        return roomID;
    }
}
