package com.cyclic.models.game.net.toclient;

import com.cyclic.configs.Enums;

import static com.cyclic.configs.Enums.Datatype.DATATYPE_ERROR;

/**
 * Created by serych on 05.04.17.
 */
public class ConnectionError {

    private final Enums.Datatype datatype = DATATYPE_ERROR;
    private Enums.DisconnectReason reasonCode;
    private String reason;

    public ConnectionError(Enums.DisconnectReason reasonCode, String reason) {
        this.reasonCode = reasonCode;
        this.reason = reason;
    }

    public String getReason() {
        return reason;
    }
}
