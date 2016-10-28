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

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.gcm.GcmPubSub;
import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.google.android.gms.iid.InstanceID;

import java.io.IOException;

/**
 * Created by yep on 25/09/16.
 */
public class Utils {

    private static String TAG = Utils.class.getSimpleName();
    private static   WifiNetworkConnectChangeReceiver mReceiver;
    private static boolean isReceiverRegister = false;
    private static boolean wifiConnected = false;
    private static boolean mobileConnected = false;
    private static final String[] TOPICS = {"global"};

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

    public static  void saveRemoteToken(String token, Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        prefs.edit().putString(context.getString(R.string.pref_GCM_token_key), token).apply();
    }

    public static String getGCMRemoteToken(Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        String token =  prefs.getString(context.getString(R.string.pref_GCM_token_key),
                context.getString(R.string.pref_GCM_token_empty_lable));
        return token;
    }


    public static  void saveLocalToken(String token, Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        prefs.edit().putBoolean(CommonConstants.PREF_IS_LOCAL_TOKEN_RECEVIED, true).apply();
        prefs.edit().putString(CommonConstants.PREF_LOCAL_TOKEN_KEY, token).apply();
    }

    public static  void  initDefaultSharePreferenceForLocalGCMToken(Context context)
    {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);

        prefs.edit().putBoolean(CommonConstants.PREF_IS_LOCAL_TOKEN_RECEVIED, false).apply();
        prefs.edit().putString(CommonConstants.PREF_LOCAL_TOKEN_KEY, "empty").apply();

    }

    public static String getGCMLocalToken(Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        String token =  prefs.getString(CommonConstants.PREF_LOCAL_TOKEN_KEY,
                context.getString(R.string.pref_GCM_token_empty_lable));
        return token;
    }

    public static String getPreferredIPAdd(Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        String ipAdd = prefs.getString(context.getString(R.string.pref_client_ip_address_key),
                context.getString(R.string.pref_client_default_ip_address));
        if((ipAdd == null)||( ipAdd.trim().length() == 0)){
            ipAdd = context.getString(R.string.pref_client_default_ip_address);
        }
        return ipAdd;
    }

    public static int getPreferredIPPort(Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);

        String port =  prefs.getString(context.getString(R.string.pref_client_ip_port_key),
                context.getString(R.string.pref_client_default_ip_port));
        
        if((port == null)|| (port.trim().length() == 0 )){
            port = context.getString(R.string.pref_client_default_ip_port);
        }
        return Integer.parseInt(port);

    }

    public static boolean checkPlayServices(Context context) {
        GoogleApiAvailability apiAvailability = GoogleApiAvailability.getInstance();
        int resultCode = apiAvailability.isGooglePlayServicesAvailable(context);
        if (resultCode != ConnectionResult.SUCCESS) {
            return false;
        }
        return true;
    }
    public static boolean isLocalTokenRecevied(Context context){
        boolean ret = false;
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        ret = prefs.getBoolean(CommonConstants.PREF_LOCAL_TOKEN_KEY, false);
        return ret;
    }

    public static void subscribeTopics(String token, Context context) throws IOException {
        GcmPubSub pubSub = GcmPubSub.getInstance(context);
        for (String topic : TOPICS) {
            pubSub.subscribe(token, "/topics/" + topic, null);
        }
    }

    public static  String startRegistration(Context context){
        String token =null;
        try{
            InstanceID instanceID = InstanceID.getInstance(context);
            token = instanceID.getToken(BuildConfig.MySenderID,
                    GoogleCloudMessaging.INSTANCE_ID_SCOPE, null);

            LogToFile.toFile("TAG", "GCM Registration Token: " + token);
            // Subscribe to topic channels



        } catch (Exception e) {
            Log.d(TAG, "Failed to complete token refresh", e);
            // If an exception happens while fetching the new token or updating our registration data
            // on a third-party server, this ensures that we'll attempt the update at a later time.

        }
        return token;
    }


}
