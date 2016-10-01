package com.radioyps.doorcontroller;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = MainActivity.class.getSimpleName();


    private static Context mContext = null;
    private static Handler mHandler;


    private  boolean isCmdFinished = true;
    private Button doorControlButton = null;
    private TextView wifiStatus = null;
    private TextView cmdStatus = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.content_main);
        mContext = this;

        doorControlButton = (Button)findViewById(R.id.toggleDoorButton);
        wifiStatus = (TextView)findViewById(R.id.wifi_status);
        cmdStatus = (TextView)findViewById(R.id.cmd_status);

        Utils.addWifiStateReceiver(mContext);

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
                }
            }
        };


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
            sendMessage(CommonConstants.MSG_UPDATE_WIFI_STATUS, getString(R.string.current_wifi_ssid) + ssid);
            Intent intent = new Intent(getApplicationContext(), PingControllerService.class);
            intent.setAction(CommonConstants.ACTION_PING);
            startService(intent);
            Log.d(TAG, "onStart()>> start service require Ping Controller ");
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
        Utils.disableWifiStateReceiver(mContext);
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
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public static void sendMessage(int messageFlag, String message ){
        Message.obtain(mHandler,
                messageFlag,
                message).sendToTarget();

    }

    public void pressDoorButton(View view) {

        Log.d(TAG, "pressDoorButton()>> ");
        /*
        * ask service to send cmd */
            Intent intent = new Intent(getApplicationContext(), PingControllerService.class);
            intent.setAction(CommonConstants.ACTION_PRESS_DOOR_BUTTON);
            startService(intent);
        /* disable botton as the door action is slow */
             MainActivity.sendMessage(CommonConstants.MSG_UPDATE_BUTTON_STATUS, CommonConstants.DISABLE_BUTTON);
    }



}
