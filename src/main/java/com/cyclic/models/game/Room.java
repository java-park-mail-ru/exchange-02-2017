package com.cyclic.models.game;

import com.cyclic.configs.Enums;
import com.cyclic.configs.RoomConfig;
import com.cyclic.models.game.net.broadcast.MoveBroadcast;
import com.cyclic.models.game.net.broadcast.PlayerDisconnectBroadcast;
import com.cyclic.models.game.net.broadcast.RoomDestructionBroadcast;
import com.cyclic.models.game.net.fromclient.Move;
import com.cyclic.models.game.net.toclient.PlayerScore;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.awt.*;
import java.util.HashSet;
import java.util.Vector;
import java.util.concurrent.ThreadLocalRandom;

import static com.cyclic.configs.Enums.Datatype.DATATYPE_ROOMINFO;
import static com.cyclic.configs.Enums.Datatype.DATATYPE_YOU_LOSE;
import static com.cyclic.configs.Enums.RoomStatus.*;

/**
 * Created by serych on 31.03.17.
 */

public class Room {
    private final Enums.Datatype datatype = DATATYPE_ROOMINFO;

    private transient int playersCount;
    private transient int startBonusCount;
    private transient int startTowerUnits;
    private transient int bonusMinValue;
    private transient int bonusMaxValue;
    private transient int fieldWidth;
    private transient int fieldHeight;
    private Vector<Player> players;
    private Long pid;
    private Enums.RoomStatus status;
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

        status = STATUS_CREATING;
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
        if (status != STATUS_CREATING)
            return false;
        player.setRoom(this);
        players.add(player);
        Point point = field.findRandomNullPoint();
        player.setBeginX(point.x);
        player.setBeginY(point.y);
        player.setUnits(startTowerUnits);

        Node mainNode = new Node(player.getId(), startTowerUnits, point.x, point.y);
        field.setNodeToPosition(point.x, point.y, mainNode);

        player.setMainNode(mainNode);
        player.getNodesMap().put(mainNode, new HashSet<>());
        if (players.size() == playersCount) {
            status = STATUS_FULL_SMB_NOT_READY;
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
            player.sendDatatype(DATATYPE_YOU_LOSE + "");
            for (Node node : player.getNodesMap().keySet()) {
                field.removeNode(node);
            }
            if (getPlayersCount() > 1) {
                broadcast(gson.toJson(new PlayerDisconnectBroadcast(player.getId())));
                if (status == STATUS_PLAYING) {
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
        if (status == STATUS_FULL_SMB_NOT_READY) {
            for (Player player : players) {
                if (!player.isReadyForGameStart()) {
                    broadcastRoomUpdate();
                    return false;
                }
            }
            status = STATUS_PLAYING;
            performingPlayerIndex = ThreadLocalRandom.current().nextInt(0, playersCount);
            pid = players.get(performingPlayerIndex).getId();
            broadcastRoomUpdate();

            field.addAndBroadcastRandomBonuses(startBonusCount);
            return true;
        }
        broadcastRoomUpdate();
        return false;
    }

    public void acceptMove(Player player, Move move) {
        if (status == STATUS_PLAYING && field != null && pid.equals(player.getId())) {
            MoveBroadcast moveBroadcast = field.acceptMove(player, move);
            if (moveBroadcast == null) {
                player.disconnectBadApi("Your are moving like an asshole");
                return;
            }
            moveBroadcast.setPid(pid);
            performingPlayerIndex += 1;
            performingPlayerIndex %= playersCount;
            pid = players.get(performingPlayerIndex).getId();
            moveBroadcast.setNextpid(pid);
            players.forEach(p -> {
                moveBroadcast.addScores(new PlayerScore(p.getId(), p.getUnits(), p.towersCount()));
            });
            broadcast(gson.toJson(moveBroadcast));
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

    public Player getPlayer(long id) {
        for (Player player : players) {
            if (player.getId() == id)
                return player;
        }
        return null;
    }
}
