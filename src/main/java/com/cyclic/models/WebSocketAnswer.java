package com.cyclic.models;

public class WebSocketAnswer {

    public static final int READY_FOR_GAME = 1;


    /**
     * ErrorCode
     */
    private int ec;
    /**
     * ErrorSting
     */
    private String es;
    /**
     * ActionCode
     */
    private int ac;

    public WebSocketAnswer(int ec, String es, int ac) {
        this.ec = ec;
        this.es = es;
        this.ac = ac;
    }

    public int getErrorCode() {
        return ec;
    }

    public String getErrorString() {
        return es;
    }

    public int getActionCode() {
        return ac;
    }
}
