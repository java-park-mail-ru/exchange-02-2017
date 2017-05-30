package com.cyclic.configs;

/**
 * Created by serych on 22.04.17.
 */
public class Enums {

    // DATATYPEs are used to send data FROM server
    public enum Datatype {
        DATATYPE_ROOMINFO,
        DATATYPE_PLAYERMOVE,
        DATATYPE_NEWBONUS,
        DATATYPE_ERROR,
        DATATYPE_HELLO,
        DATATYPE_YOU_WIN,
        DATATYPE_PLAYER_DISCONNECT,
        DATATYPE_ROOMMANAGER_UPDATE,
        DATATYPE_PONG
    }

    // ACTIONs are used to send data TO server
    public enum Action {
        ACTION_GIVE_ME_ROOM,
        ACTION_GAME_MOVE,
        ACTION_EXIT_ROOM,
        ACTION_PING
    }

    // DISCONNECT Reasons
    public enum DisconnectReason {
        DISCONNECT_REASON_API_HACKER,
        DISCONNECT_REASON_NOT_LOGINED
    }

    // ROOM statuses
    public enum RoomStatus {
        STATUS_CREATING,
        STATUS_PLAYING,
        STATUS_FINISHED
    }

    // MOVE results
    public enum MoveResult {
        ACCEPT_OK,
        ACCEPT_WIN,
        ACCEPT_LOSE,
        ACCEPT_TIMEOUT
    }
}
