package com.cyclic.models.game.net;

import static com.cyclic.configs.Constants.DATATYPE_HELLO;

/**
 * Created by serych on 06.04.17.
 */
public class HelloMessage {
    private final int datatype = DATATYPE_HELLO;
    private String nickname;
    private long id;

    public HelloMessage(String nickname, long id) {
        this.nickname = nickname;
        this.id = id;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }
}
