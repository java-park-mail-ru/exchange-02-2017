package com.cyclic.models.game.net;

import static com.cyclic.controllers.WebSocketController.DATATYPE_ERROR;

/**
 * Created by serych on 05.04.17.
 */
public class ConnectionError {

    public static final int DISCONNECT_REASON_API_HACKER = 1;
    public static final int DISCONNECT_REASON_NOT_LOGINED = 1;

    private final int datatype = DATATYPE_ERROR;
    private int code;
    private String data;

    public ConnectionError(int code, String data) {
        this.code = code;
        this.data = data;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }
}
