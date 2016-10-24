package com.radioyps.doorcontroller;

import android.content.Context;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.preference.PreferenceManager;
import android.util.Log;

/**
 * Created by yep on 25/09/16.
 */
public class Utils {

    private static String TAG = Utils.class.getSimpleName();
    private static   WifiNetworkConnectChangeReceiver mReceiver;
    private static boolean isReceiverRegister = false;
    private static boolean wifiConnected = false;
    private static boolean mobileConnected = false;

    private  static final  String UNKNOWN_SSID_1 = "unknown ssid";
    private  static final  String UNKNOWN_SSID_2 = "0x";
    public static boolean isStringBlank(String input){
        boolean ret = false;
        if((input == null)||(input.trim() == "")){
            ret = true;
        }
        return ret;
    }

    public static boolean isWifiConnected(Context context){

            ConnectivityManager connMgr =
                    (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

            NetworkInfo activeInfo = connMgr.getActiveNetworkInfo();
            if (activeInfo != null && activeInfo.isConnected()) {
                wifiConnected = activeInfo.getType() == ConnectivityManager.TYPE_WIFI;
                mobileConnected = activeInfo.getType() == ConnectivityManager.TYPE_MOBILE;
            } else {
                wifiConnected = false;
                mobileConnected = false;
            }
        if (mobileConnected)
            Log.i(TAG, "isWifiConnected()>> mobile connected");
        else
            Log.i(TAG, "isWifiConnected()>> mobile connected");

        if (wifiConnected)
        Log.i(TAG, "isWifiConnected()>> WIFI connected");
        else
            Log.i(TAG, "isWifiConnected()>> WIFI connected");

        if(getCurrentSsid(context) == null){
            wifiConnected = false;
        }
        return wifiConnected;

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
                Log.i(TAG, "getCurrentSsid()>> current SSID: " +ssid);
                if((ssid.indexOf(UNKNOWN_SSID_1)!= -1)
                ||(ssid.equalsIgnoreCase(UNKNOWN_SSID_2))){
                    Log.i(TAG, "getCurrentSsid()>> ssid not vaild, replaced with null");
                    ssid = null;
                }
            }
        }

        return ssid;
    }




    public static void addWifiStateReceiver(Context context) {
        IntentFilter filter = new IntentFilter();
        filter.addAction(WifiManager.NETWORK_STATE_CHANGED_ACTION);
        filter.addAction(WifiManager.WIFI_STATE_CHANGED_ACTION);
        filter.addAction(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION);
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

    public static  void saveToken(String token, Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        prefs.edit().putString(context.getString(R.string.pref_GCM_token_key), token).apply();
    }

    public static String getGCMToken(Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        String token =  prefs.getString(context.getString(R.string.pref_GCM_token_key),
                context.getString(R.string.pref_GCM_token_empty_lable));
        return token;
    }

    public static String getPreferredIPAdd(Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        return prefs.getString(context.getString(R.string.pref_client_ip_address_key),
                context.getString(R.string.pref_client_default_ip_address));
    }

    public static int getPreferredIPPort(Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);

        String port =  prefs.getString(context.getString(R.string.pref_client_ip_port_key),
                context.getString(R.string.pref_client_default_ip_port));
        

        return Integer.valueOf(port);

    }

}
