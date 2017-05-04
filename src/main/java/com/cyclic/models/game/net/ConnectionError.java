package com.cyclic.models.game.net;

import static com.cyclic.configs.Constants.DATATYPE_ERROR;

/**
 * Created by serych on 05.04.17.
 */
public class ConnectionError {

    private final int datatype = DATATYPE_ERROR;
    private int code;
    private String reason;

    public ConnectionError(int code, String reason) {
        this.code = code;
        this.reason = reason;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }
}
