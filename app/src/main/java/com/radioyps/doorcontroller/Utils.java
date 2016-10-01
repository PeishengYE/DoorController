package com.radioyps.doorcontroller;

import android.content.Context;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;

/**
 * Created by yep on 25/09/16.
 */
public class Utils {

    private static   WifiNetworkConnectChangeReceiver mReceiver;
    private static boolean isReceiverRegister = false;
    public static boolean isStringBlank(String input){
        boolean ret = false;
        if((input == null)||(input.trim() == "")){
            ret = true;
        }
        return ret;
    }

    public static boolean isWifiConnected(Context context){
        boolean ret = false;
        String currentSsid = getCurrentSsid(context);
        if(currentSsid != null){
            ret = true;
        }else
            ret = false;
        return ret;
    }
    public static String getCurrentSsid(Context context) {
        String ssid = null;
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = cm.getActiveNetworkInfo();
        if (networkInfo == null) {
            return null;
        }

        if (networkInfo.isConnected()) {
            final WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
            final WifiInfo connectionInfo = wifiManager.getConnectionInfo();
            if (connectionInfo != null && !isStringBlank(connectionInfo.getSSID())) {
                ssid = connectionInfo.getSSID();
            }
        }

        return ssid;
    }




    public static void addWifiStateReceiver(Context context) {
        IntentFilter filter = new IntentFilter();
        filter.addAction(WifiManager.NETWORK_STATE_CHANGED_ACTION);
        filter.addAction(WifiManager.WIFI_STATE_CHANGED_ACTION);
        filter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
        mReceiver = new WifiNetworkConnectChangeReceiver();
        context.registerReceiver(mReceiver, filter);
        isReceiverRegister = true;
        }

    public static void disableWifiStateReceiver(Context context) {
        if(isReceiverRegister){
            context.unregisterReceiver(mReceiver);
            isReceiverRegister = false;
        }

    }

}
