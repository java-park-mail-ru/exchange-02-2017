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
        DATATYPE_ROOM_DESTRUCTION,
        DATATYPE_PLAYER_DISCONNECT,
        DATATYPE_YOU_LOSE
    }

    // ACTIONs are used to send data TO server
    public enum Action {
        ACTION_READY_FOR_ROOM_SEARCH,
        ACTION_READY_FOR_GAME_START,
        ACTION_GAME_MOVE
    }

    // DISCONNECT Reasons
    public enum DisconnectReason{
        DISCONNECT_REASON_API_HACKER,
        DISCONNECT_REASON_NOT_LOGINED
    }

    // ROOM statuses
    public enum RoomStatus {
        STATUS_CREATING,
        STATUS_PLAYING,
        STATUS_FULL_SMB_NOT_READY
    }

    // MOVE results
    public enum MoveResult {
        ACCEPT_OK,
        ACCEPT_WIN,
        ACCEPT_LOSE
    }
}
