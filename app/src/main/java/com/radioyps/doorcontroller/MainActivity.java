package com.radioyps.doorcontroller;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = MainActivity.class.getSimpleName();



    private static Handler mHandler;

    private static final int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;


    private Button doorControlButton = null;
    private Button doorRemoteButton = null;
    private TextView wifiStatus = null;
    private TextView cmdStatus = null;
    private TextView gcmStatus = null;
    private static String GCM_token = null;
    private static Context mContext = null;
    private  static final int MY_PERMISSIONS_REQUEST_USE_CAMERA = 0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.content_main);


        doorControlButton = (Button)findViewById(R.id.toggleDoorButton);
        doorRemoteButton = (Button)findViewById(R.id.gcmSending);
        wifiStatus = (TextView)findViewById(R.id.wifi_status);
        cmdStatus = (TextView)findViewById(R.id.cmd_status);
        gcmStatus = (TextView)findViewById(R.id.gcm_status);

        doorRemoteButton.setBackgroundResource(R.drawable.gcm_icon);
        mContext = this;
        //Utils.addWifiStateReceiver(mContext);

        mHandler = new Handler() {
            public void handleMessage(Message msg) {
                switch (msg.what) {
                    case CommonConstants.MSG_UPDATE_WIFI_STATUS:
                        wifiStatus.setText((String) msg.obj);
                        break;
                    case CommonConstants.MSG_UPDATE_BUTTON_STATUS:
                        String messg =  (String) msg.obj;
                        //doorControlButton.setText(messg);

                        if(messg.equalsIgnoreCase(CommonConstants.ENABLE_BUTTON)){
                            doorControlButton.setEnabled(true);
                            doorControlButton.setText(getString(R.string.button_available));
                        }else if(messg.equalsIgnoreCase(CommonConstants.DISABLE_BUTTON)){
                            doorControlButton.setText(getString(R.string.button_not_available));
                            doorControlButton.setEnabled(false);
                        }
                        break;
                    case CommonConstants.MSG_UPDATE_CMD_STATUS:
                        cmdStatus.setText((String) msg.obj);
                        break;
                    case CommonConstants.MSG_GCM_CMD_STATUS:
                        gcmStatus.setText((String) msg.obj);
                        break;
                }
            }
        };
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
        ActivityCompat.requestPermissions(this,
                new String[]{Manifest.permission.CAMERA},
                MY_PERMISSIONS_REQUEST_USE_CAMERA);


        if (checkPlayServices()) {
            GCMGateWay.start("MainActivity start", this);
        }

    }


    private boolean checkPlayServices() {
        GoogleApiAvailability apiAvailability = GoogleApiAvailability.getInstance();
        int resultCode = apiAvailability.isGooglePlayServicesAvailable(this);
        if (resultCode != ConnectionResult.SUCCESS) {
            if (apiAvailability.isUserResolvableError(resultCode)) {
                apiAvailability.getErrorDialog(this, resultCode, PLAY_SERVICES_RESOLUTION_REQUEST)
                        .show();
            } else {
                Log.i(TAG, "This device is not supported.");
                finish();
            }
            return false;
        }
        return true;
    }

    @Override
    protected void onStart() {
        super.onStart();

        doorControlButton.setEnabled(false);
        doorControlButton.setText(getString(R.string.button_not_available));
        PingControllerService.enableConnect();

        sendMessage(CommonConstants.MSG_UPDATE_CMD_STATUS, getString(R.string.waiting_wifi_connected));
        String ssid = Utils.getCurrentSsid(mContext);
        if(ssid != null){
            sendMessage(CommonConstants.MSG_UPDATE_WIFI_STATUS, getString(R.string.current_wifi_ssid) + " "  + ssid);
            Intent intent = new Intent(getApplicationContext(), PingControllerService.class);
            intent.setAction(CommonConstants.ACTION_PING);
            startService(intent);
            Log.d(TAG, "onStart()>> start service require Ping Controller ");
        }else{
            sendMessage(CommonConstants.MSG_UPDATE_WIFI_STATUS,getString(R.string.no_wifi_connected));
        }


    }

    @Override
    protected void onStop() {
        super.onStop();
        /*Stop trying connecting to controller */
        PingControllerService.disableConnect();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            startActivity(new Intent(this, SettingsActivity.class ));
            return true;
        }else if(id == R.id.action_scan_wifi){
            startActivity(new Intent(this, WifiScanResult.class));
            return true;
        }else if(id == R.id.action_scan_token){
            startActivity(new Intent(this, QRCodeScanner.class ));
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public static void sendMessage(int messageFlag, String message ){

        String mesg = null;

        if(message.equalsIgnoreCase(CommonConstants.FLAG_GCM_FAILURE)){
            mesg = mContext.getString(R.string.remote_door_cmd_in_failure);
        }else if(message.equalsIgnoreCase(CommonConstants.FLAG_GCM_OK)){
            mesg = mContext.getString(R.string.remote_door_cmd_in_success);
        }else
            mesg = message;

        Message.obtain(mHandler,
                messageFlag,
                mesg).sendToTarget();

    }

    public void pressDoorButton(View view) {

        Log.d(TAG, "pressDoorButton()>> ");
        /*
        * ask service to send cmd */
            Intent intent = new Intent(getApplicationContext(), PingControllerService.class);
            intent.setAction(CommonConstants.ACTION_PRESS_DOOR_BUTTON);
            startService(intent);
        /* disable botton as the door action is slow */
            // MainActivity.sendMessage(CommonConstants.MSG_UPDATE_BUTTON_STATUS, CommonConstants.DISABLE_BUTTON);
    }

    public void pressRemoteButton(View view) {

        Log.d(TAG, "pressDoorRemoteControlButton()>> ");
        String token = Utils.getGCMRemoteToken(this);
        if(token.equalsIgnoreCase(this.getString(R.string.pref_GCM_token_empty_lable))){
            Log.d(TAG, "pressDoorRemoteControlButton()>> GCM token is empty ");
            Toast.makeText(this, R.string.waring_gcm_token_empty, Toast.LENGTH_LONG).show();
            startActivity(new Intent(this, QRCodeScanner.class ));

        }else{
        /* * ask service to send cmd */
            Log.d(TAG, "pressDoorRemoteControlButton()>> sending remote cmd ");
//            Intent intent = new Intent(getApplicationContext(), PingControllerService.class);
//            intent.setAction(CommonConstants.ACTION_PRESS_REMOTE_BUTTON);
//            startService(intent);
            MainActivity.sendMessage(CommonConstants.MSG_GCM_CMD_STATUS, getString(R.string.remote_door_cmd_in_progress));
            //sendGCM(BuildConfig.DOORCONFIRMKAY);
            sendGCM("HelloWorld");
        }
    }

    private void sendGCM(String message){
        GcmSendTask gcmTask = new GcmSendTask();
        String [] cmd = new String[] {message, ""};
        gcmTask.execute(cmd);
    }

    public static String getRemoteToken(){
        GCM_token = Utils.getGCMRemoteToken(mContext);
        return  GCM_token;
    }

    public static String getLocalToken(){
        GCM_token = Utils.getGCMLocalToken(mContext);
        return  GCM_token;
    }
}
