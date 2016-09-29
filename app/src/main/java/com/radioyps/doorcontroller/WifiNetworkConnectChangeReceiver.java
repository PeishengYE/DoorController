package com.radioyps.doorcontroller;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;
import android.os.Parcelable;
import android.util.Log;

/**
 * Created by developer on 28/09/16.
 */
public class WifiNetworkConnectChangeReceiver extends BroadcastReceiver {
    private final static String TAG = WifiNetworkConnectChangeReceiver.class.getSimpleName();
    @Override
    public void onReceive(Context context, Intent intent) {

        Log.i(TAG, "onReceive()>> ");
        String action = intent.getAction();

        if (WifiManager.NETWORK_STATE_CHANGED_ACTION.equals (action)) {
            NetworkInfo netInfo = intent.getParcelableExtra(WifiManager.EXTRA_NETWORK_INFO);
            if (ConnectivityManager.TYPE_WIFI == netInfo.getType()) {
                NetworkInfo.State state = netInfo.getState();
                if(state == NetworkInfo.State.CONNECTED){

                    Log.i(TAG, "onReceive()>> WIFI connected");
                    String ssid = Utils.getCurrentSsid(context);
                    if(ssid != null)
                    MainActivity.sendMessage(CommonConstants.MSG_UPDATE_WIFI_STATUS, "Current WiFi: " + ssid);
                }else {
                    MainActivity.sendMessage(CommonConstants.MSG_UPDATE_WIFI_STATUS, "WiFi disconnect");
                    Log.i(TAG, "onReceive()>> WIFI disconnected");
                }
            }
        }
        /*
        if (WifiManager.NETWORK_STATE_CHANGED_ACTION.equals(intent.getAction())) {
            Parcelable parcelableExtra = intent
                    .getParcelableExtra(WifiManager.EXTRA_NETWORK_INFO);
            if (null != parcelableExtra) {
                NetworkInfo networkInfo = (NetworkInfo) parcelableExtra;
                NetworkInfo.State state = networkInfo.getState();
                boolean isConnected = state == NetworkInfo.State.CONNECTED;
                Log.i(TAG, "isConnected " + isConnected);
                if (isConnected) {
                } else {

                }
            }
        }*/
        Log.i(TAG, "onReceive()<< ");
    }

    public WifiNetworkConnectChangeReceiver() {
        super();
    }
}
