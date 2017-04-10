package com.cyclic;

/**
 * Created by serych on 02.04.17.
 */
public class LOG {
    public static void errorConsole(Exception e) {
        e.printStackTrace();
    }
    public static void error(Exception e) {
        //e.printStackTrace();
    }

    public static void webSocketLog(String string) {
        System.out.println(string);
    }
}
