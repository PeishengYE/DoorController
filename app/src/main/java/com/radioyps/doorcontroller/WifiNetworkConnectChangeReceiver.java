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
                    MainActivity.sendMessage(CommonConstants.MSG_UPDATE_WIFI_STATUS, context.getString(R.string.current_wifi_ssid) + ssid);
                    PingControllerService.enableConnect();
                    Intent intentStartService = new Intent(context, PingControllerService.class);
                    intentStartService.setAction(CommonConstants.ACTION_PING);
                    context.startService(intentStartService);

                    Log.d(TAG, "onStart()>> start service require Ping Controller ");
                }else {
                    MainActivity.sendMessage(CommonConstants.MSG_UPDATE_WIFI_STATUS, context.getString(R.string.no_wifi_connected));
                    MainActivity.sendMessage(CommonConstants.MSG_UPDATE_BUTTON_STATUS, CommonConstants.DISABLE_BUTTON);
                    MainActivity.sendMessage(CommonConstants.MSG_UPDATE_CMD_STATUS, context.getString(R.string.waiting_wifi_connected));
                    Log.i(TAG, "onReceive()>> WIFI disconnected, stop Ping controller");
                    PingControllerService.disableConnect();
                }
            }else{
                //Log.i(TAG, "onReceive()>> WIFI disconnected, stop Ping controller");
            }
        }

        Log.i(TAG, "onReceive()<< ");
    }

    public WifiNetworkConnectChangeReceiver() {
        super();
    }
}
