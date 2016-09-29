package com.radioyps.doorcontroller;

/**
 * Created by developer on 28/09/16.
 */
public final class CommonConstants {

    public CommonConstants() {

        // don't allow the class to be instantiated
    }
    public static final String ACTION_PING = "com.radioyps.doorcontroller.ACTION_PING";
    public static final String ACTION_PRESS_DOOR_BUTTON = "com.radioyps.doorcontroller.ACTION_PRESS_DOOR_BUTTON";
    public static final int MSG_UPDATE_WIFI_STATUS = 0x10;
    public static final int MSG_UPDATE_BUTTON_STATUS = 0x11;
    public static final int MSG_UPDATE_CMD_STATUS = 0x12;

    public static final String CMD_PING_CONTROLLER = "I'm Alive";
    public static final String FLAG_CONTROLLER_ALIVE = "I'm Alive";

    public static int connectPort = 5028;
    public static String CMD_PRESS_DOOR_BUTTON = "A412..&35?@!";

    public final static int SOCKET_TIMEOUT = 10 * 000; /*10 seconds */
    public final static String IP_ADDR = "192.168.12.238";
    public final static String NETWORK_ERROR = "network error";
}