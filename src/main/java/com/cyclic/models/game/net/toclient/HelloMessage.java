package com.cyclic.models.game.net.toclient;

import com.cyclic.configs.Enums;

import static com.cyclic.configs.Enums.Datatype.DATATYPE_HELLO;

/**
 * Created by serych on 06.04.17.
 */
public class HelloMessage {
    private final Enums.Datatype datatype = DATATYPE_HELLO;
    private String nickname;

    public HelloMessage(String nickname) {
        this.nickname = nickname;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }
}
