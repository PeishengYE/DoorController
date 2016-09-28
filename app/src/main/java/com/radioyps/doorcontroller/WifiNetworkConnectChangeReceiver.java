package com.radioyps.doorcontroller;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;

/**
 * Created by developer on 28/09/16.
 */
public class WifiNetworkConnectChangeReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();

        if (WifiManager.NETWORK_STATE_CHANGED_ACTION.equals (action)) {
            NetworkInfo netInfo = intent.getParcelableExtra(WifiManager.EXTRA_NETWORK_INFO);
            if (ConnectivityManager.TYPE_WIFI == netInfo.getType()) {

            }
        }
    }

    public WifiNetworkConnectChangeReceiver() {
        super();
    }
}
