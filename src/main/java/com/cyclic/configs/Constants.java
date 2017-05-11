package com.cyclic.configs;

/**
 * Created by serych on 22.04.17.
 */
public class Constants {

    // DATATYPEs are used to send data FROM server
    public static final int DATATYPE_ROOMINFO = 1;
    public static final int DATATYPE_PLAYERMOVE = 2;
    public static final int DATATYPE_NEWBONUS = 3;
    public static final int DATATYPE_ERROR = 4;
    public static final int DATATYPE_HELLO = 5;
    public static final int DATATYPE_ROOM_DESTRUCTION = 6;
    public static final int DATATYPE_PLAYER_DISCONNECT = 7;

    // ACTIONs are used to send data TO server
    public static final int ACTION_READY_FOR_ROOM_SEARCH = 1;
    public static final int ACTION_READY_FOR_GAME_START = 2;
    public static final int ACTION_GAME_MOVE = 3;

    // NODE types
    public static final int NODE_TOWER = 0;
    public static final int NODE_BONUS = 1;

    // DISCONNECT Reasons
    public static final int DISCONNECT_REASON_API_HACKER = 1;
    public static final int DISCONNECT_REASON_NOT_LOGINED = 1;

    // ROOM statuses
    public static final int STATUS_CREATING = 0;
    public static final int STATUS_PLAYING = 1;
    public static final int STATUS_FULL_SMB_NOT_READY = 2;
}
