package com.cyclic.models.game;

import com.cyclic.configs.Enums;
import com.cyclic.configs.RoomConfig;
import com.cyclic.models.game.net.fromclient.Move;
import com.cyclic.models.game.net.toclient.PlayerScore;
import com.cyclic.models.game.net.toclient.broadcast.MoveBroadcast;
import com.cyclic.models.game.net.toclient.broadcast.PlayerDisconnectBroadcast;
import com.cyclic.models.game.net.toclient.broadcast.WinBroadcast;
import com.cyclic.services.game.RoomManager;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.awt.*;
import java.util.*;
import java.util.Queue;

import static com.cyclic.configs.Enums.Datatype.DATATYPE_ROOMINFO;
import static com.cyclic.configs.Enums.RoomStatus.*;

/**
 * Created by serych on 31.03.17.
 */

public class Room {
    private final Enums.Datatype datatype = DATATYPE_ROOMINFO;
    private final LinkedList<Player> players;
    private transient final LinkedList<Player> spectators;
    private int capacity;
    private transient int startBonusCount;
    private transient int startTowerUnits;
    private transient int bonusMinValue;
    private transient int bonusMaxValue;
    private double moveRadius;
    private double moveTimeSeconds;
    private int fieldWidth;
    private int fieldHeight;
    private Long pid;
    private Enums.RoomStatus status;

    private transient Gson gson;
    private transient GameField field;
    private transient ArrayList<Integer> freeColors;
    private transient RoomConfig roomConfig;
    private transient RoomManager roomManager;
    private transient Timer moveTimer;
    private transient MoveTimerTask moveTimerTask;
    private transient Queue<Player> moveQueue;


    public Room(RoomManager roomManager, RoomConfig roomConfig) {
        this.roomConfig = roomConfig;
        this.roomManager = roomManager;
        capacity = roomConfig.getPlayersCount();
        startBonusCount = roomConfig.getStartBonusCount();
        startTowerUnits = roomConfig.getStartTowerUnits();
        bonusMaxValue = roomConfig.getBonusMaxValue();
        bonusMinValue = roomConfig.getBonusMinValue();
        fieldHeight = roomConfig.getFieldHeight();
        fieldWidth = roomConfig.getFieldWidth();
        moveRadius = roomConfig.getMoveRadius();
        moveTimeSeconds = roomConfig.getMoveTimeSeconds();
        moveTimer = new Timer();
        moveTimerTask = new MoveTimerTask();


        freeColors = new ArrayList<>(capacity);
        for (int i = 0; i < capacity; i++) {
            freeColors.add(i);
        }
        status = STATUS_CREATING;
        players = new LinkedList<>();
        spectators = new LinkedList<>();
        field = new GameField(this);
        moveQueue = new LinkedList<>();
        gson = new GsonBuilder().create();
        pid = null;
    }

    public RoomConfig getRoomConfig() {
        return roomConfig;
    }

    public int getCapacity() {
        return capacity;
    }

    public int getPlayersCount() {
        return players.size();
    }

    public boolean isEmpty() {
        return players.isEmpty();
    }

    public boolean isFull() {
        return players.size() == capacity;
    }

    public boolean isAlmostFull() {
        return players.size() == capacity - 1;
    }

    /**
     * Adds player to this room. Starts room if it is full
     *
     * @param player Player to add
     * @return True if the player was added. False if error has occurred
     */
    public synchronized boolean addPlayer(Player player) {
        if (status != STATUS_CREATING)
            return false;
        player.setRoom(this);
        players.add(player);
        if (players.size() == capacity)
            status = STATUS_PLAYING;

        Thread thread = new Thread(() -> {

            player.setColor(freeColors.get(0));
            player.setId((long) freeColors.get(0));
            freeColors.remove(0);
            Point point = field.findRandomNullSpawnPoint();
            player.resetNodesMap();
            player.setBeginX(point.x);
            player.setBeginY(point.y);
            player.setUnits(startTowerUnits);

            Node mainNode = new Node(player.getId(), startTowerUnits, point.x, point.y);
            field.setNodeToPosition(point.x, point.y, mainNode);

            player.setMainNode(mainNode);
            player.getNodesMap().put(mainNode, new HashSet<>());

            // Start game if room is full
            if (status == STATUS_PLAYING) {
                Collections.shuffle(players);
                moveQueue.addAll(players);
                generateNextPid();
                broadcastRoomUpdate();
                field.addAndBroadcastRandomBonuses(startBonusCount);
                moveTimer.schedule(moveTimerTask, (long) moveTimeSeconds * 1000);
            } else
                broadcastRoomUpdate();
        });
        thread.start();

        return true;
    }

    /**
     * @param player Player to remove from this room
     * @return If the last player in this room, give him another room
     */
    public synchronized void removePlayer(Player player, boolean duringMove) {
        if (players.remove(player)) {
            long id = player.getId();
            player.setRoom(null);
            player.setId(null);
            moveQueue.remove(player);
            freeColors.add(player.getColor());
            if (status == STATUS_CREATING) {
                roomManager.getBroadcast().getRoomData(capacity).setQueue(getPlayersCount());
                roomManager.updateAndBroadcastJson();
                field.addPossibleSpawnPoints(player);
                broadcastRoomUpdate();
                return;
            }
            // Playing game
            if (players.size() == 0) {
                return;
            }
            for (Node node : player.getNodesMap().keySet()) {
                field.removeNode(node);
            }
            if (getPlayersCount() > 1) { // Game did not end
                if (pid == id) {
                    generateNextPid();
                }
                if (!duringMove) {
                    broadcast(gson.toJson(new PlayerDisconnectBroadcast(id, pid)));
                }
            } else { // Player WIN!!
                status = STATUS_FINISHED;
                if (moveTimer != null) {
                    moveTimer.cancel();
                }
                Player lastPlayer = players.get(0);
                lastPlayer.sendString(gson.toJson(new WinBroadcast()));
                RoomManager.accountService.updateUserHighscore(lastPlayer.getNickname(), lastPlayer.unitsCount());
                removePlayer(lastPlayer, false);
                addSpectator(lastPlayer);
            }
        }
    }

    /**
     * Broadcasts this room info to all players in this room
     */
    private void broadcastRoomUpdate() {
        for (Player player : players) {
            player.sendString(gson.toJson(this));
        }

        for (Player player : spectators) {
            player.sendString(gson.toJson(this));
        }
    }

    /**
     * Broadcasts data to all players in this room
     *
     * @param data Data to broadcast
     */
    public void broadcast(String data) {
        players.forEach(player -> {
            player.sendString(data);
        });
        spectators.forEach(player -> {
                    player.sendString(data);
                }
        );
    }

    /**
     * If {@param move} is null, it will say, that user timed out it's move and meth
     */
    // TODO Handle move is null, then just pass move to another player and notify other players about it
    public synchronized void acceptMove(Player player, AbstractMove move) {
        if (status == STATUS_PLAYING) {
            if (pid.equals(player.getId())) {
                moveTimer.cancel();
                MoveBroadcast moveBroadcast = null;
                // Handle timeout
                if (move instanceof TimeoutMove) {
                    moveBroadcast = new MoveBroadcast();
                    moveBroadcast.setResult(Enums.MoveResult.ACCEPT_TIMEOUT);
                }
                if (move instanceof Move) {
                    moveBroadcast = field.acceptMove(player, (Move) move);
                }
                if (moveBroadcast == null) {
                    player.disconnectBadApi("You cannot move like this");
                    return;
                }

                moveBroadcast.setPid(pid);
                generateNextPid();
                if (moveBroadcast.getDeadpid() != null && Objects.equals(moveBroadcast.getDeadpid(), pid)) {
                    generateNextPid();
                }
                moveBroadcast.setNextpid(pid);

                for (Player p : players) {
                    if (moveBroadcast.getDeadpid() == null || p.getId() != moveBroadcast.getDeadpid()) {
                        moveBroadcast.addScores(new PlayerScore(p.getId(), p.unitsCount(), p.towersCount()));
                    }
                }

                moveBroadcast.sortScores();
                broadcast(gson.toJson(moveBroadcast));

                if (moveBroadcast.getDeadpid() != null) {
                    Player loser = getPlayer(moveBroadcast.getDeadpid());
                    removePlayer(loser, true);
                    addSpectator(loser);
                }

                if (getPlayersCount() != 0) {
                    moveTimer = new Timer();
                    moveTimerTask = new MoveTimerTask();
                    moveTimer.schedule(moveTimerTask, (long) moveTimeSeconds * 1000);
                }

            }
        } else {
            player.disconnectBadApi("Cannot accept move. Room is not playing");
        }
    }

    public void removeSpectator(Player player) {
        spectators.remove(player);
        player.setRoom(null);
    }

    public void addSpectator(Player player) {
        spectators.add(player);
        player.setRoom(this);
    }

    private void generateNextPid() {
        Player p = moveQueue.poll();
        pid = p.getId();
        moveQueue.add(p);
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

    public double getMoveRadius() {
        return moveRadius;
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

    public Player getPlayer(long id) {
        for (Player player : players) {
            if (player.getId() == id)
                return player;
        }
        return null;
    }

    public boolean isSpectator(Player player) {
        return spectators.contains(player);
    }

    public boolean isPLayer(Player player) {
        return players.contains(player);
    }

    public Enums.RoomStatus getStatus() {
        return status;
    }

    private class MoveTimerTask extends TimerTask {

        @Override
        public void run() {
            acceptMove(getPlayer(pid), new TimeoutMove());
        }
    }
}
