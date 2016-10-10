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
    public static final String ACTION_PRESS_REMOTE_BUTTON = "com.radioyps.doorcontroller.ACTION_PRESS_REMOTE_BUTTON";
    public static final String ACTION_STOP_CONNECTING = "com.radioyps.doorcontroller.ACTION_STOP_CONNECTING";
    public static final String ACTION_START_CONNECTING = "com.radioyps.doorcontroller.ACTION_START_CONNECTING";

    public static final String FLAG_GCM_OK = "com.radioyps.doorcontroller.FLAG_GCM_OK";
    public static final String FLAG_GCM_FAILURE = "com.radioyps.doorcontroller.FLAG_GCM_FAILURE";

    public static final int MSG_UPDATE_WIFI_STATUS = 0x10;
    public static final int MSG_UPDATE_BUTTON_STATUS = 0x11;
    public static final int MSG_UPDATE_CMD_STATUS = 0x12;
    public static final int MSG_GCM_CMD_STATUS = 0x13;

    public static final String CMD_PING_CONTROLLER = "78*(^@/uid";
    public static final String PING_ACK = "^3234adsfa/?";
    public static final String DISABLE_BUTTON =  "com.radioyps.doorcontroller.DISABLE_BUTTON";
    public static final String ENABLE_BUTTON =  "com.radioyps.doorcontroller.ENABLE_BUTTON";;



    public static int connectPort = 5028;
    public static String CMD_PRESS_DOOR_BUTTON = "A412..&35?@!";
    public static String ACK_PRESS_DOOR_BUTTON =  "B835??/!xx";
    public final static int SOCKET_TIMEOUT = 10 * 000; /*10 seconds */
    // ip address for testing
   // public final static String IP_ADDR = "192.168.12.238";
    // ip address for the real door controller
    public final static String IP_ADDR = "192.168.12.240";
    public final static String NETWORK_ERROR = "network error";
}