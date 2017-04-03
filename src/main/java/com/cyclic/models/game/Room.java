package com.cyclic.models.game;

import com.cyclic.models.game.net.PlayerMoveAnswer;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.Vector;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Created by serych on 31.03.17.
 */
public class Room {
    public static final int STATUS_CREATING = 0;
    public static final int STATUS_PLAYING = 1;
    public static final int STATUS_READY = 2;

    public static final int PLAYERS_COUNT = 2;
    public static final int FIELD_WIDTH  = 1000;
    public static final int FIELD_HEIGHT = 1000;

    private Vector<Player> players;
    private transient GameField field;
    private int performingId;
    private int status;
    private long roomID;
    private Gson gson;

    public Room(long roomID) {
        status = STATUS_CREATING;
        players = new Vector<>(4);
        field = null;
        gson = new GsonBuilder().create();
        this.roomID = roomID;
    }

    public int getSessionsCount() {
        return players.size();
    }

    public boolean isEmpty() {
        return players.isEmpty();
    }

    public boolean addPlayer(Player player) {
        if (status != STATUS_CREATING)
            return false;
        player.setNickname("Nick");
        player.setId(players.size());
        player.setRoom(this);
        players.add(player);
        if (players.size() == PLAYERS_COUNT) {
            status = STATUS_READY;
        }
        broadcastRoomUpdate();
        return true;
    }

    private void broadcastRoomUpdate() {
        for (Player player: players) {
            Gson gson = new GsonBuilder().create();
            player.send(gson.toJson(this));
        }
    }

    public void broadcast(String data) {
        for (Player player: players) {
            player.send(data);
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
            field = new GameField(this, FIELD_HEIGHT, FIELD_WIDTH);
            broadcastRoomUpdate();
            return true;
        }
        broadcastRoomUpdate();
        return false;
    }

    public int getStatus() {
        return status;
    }

    public boolean deletePlayer(Player player) {
        for (Player p: players) {
            if (p == player) {
                players.remove(player);
                field.stopGame();
                field = null;
                status = STATUS_CREATING;
                broadcastRoomUpdate();
                return true;
            }
        }
        return false;
    }

    public void handlePlayersMove(Player player, Moves moves) {
        if (status == STATUS_PLAYING && field != null && performingId == player.getId()) {
            field.setPossibleMoves(moves);
            broadcast(gson.toJson(new PlayerMoveAnswer(player.getId(), PlayerMoveAnswer.ANSWER_MOVE_TEMP, moves)));
        }
        else {
            player.disconnectBadApi();
        }
    }

    public void acceptMove(Player player) {
        if (status == STATUS_PLAYING && field != null && performingId == player.getId()) {
            if (!field.acceptMove())
                player.disconnectBadApi();
            broadcast(gson.toJson(new PlayerMoveAnswer(player.getId(), PlayerMoveAnswer.ANSWER_MOVE_NORM, field.getPossibleMoves())));
            performingId += 1;
            performingId %= PLAYERS_COUNT;
            broadcastRoomUpdate();
        }
        else {
            player.disconnectBadApi();
        }
    }

    public int getPerformingId() {
        return performingId;
    }
}
