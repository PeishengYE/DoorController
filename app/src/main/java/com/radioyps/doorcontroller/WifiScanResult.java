package com.radioyps.doorcontroller;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class WifiScanResult extends AppCompatActivity implements View.OnClickListener {

   private static final  String TAG = WifiScanResult.class.getSimpleName();
   private  WifiManager wifi;
   private  ListView wifi_ssid_list;
   private  TextView textStatus;
   private  Button buttonScan;
   private  static int wifiResultSize = 0;
   private  static List<ScanResult> wifiScanResults;
    private  BroadcastReceiver bReciver;
    String ITEM_KEY = "key";
    ArrayList<HashMap<String, String>> arraylist = new ArrayList<HashMap<String, String>>();
    SimpleAdapter adapter;


    /* Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wifi_scan_result);

        textStatus = (TextView) findViewById(R.id.wifi_scan_status);
        buttonScan = (Button) findViewById(R.id.active_wifi_scan);
        buttonScan.setOnClickListener(this);
        wifi_ssid_list = (ListView) findViewById(R.id.wifi_net_list);

        wifi = (WifiManager) getSystemService(Context.WIFI_SERVICE);
        if (wifi.isWifiEnabled() == false) {
            Toast.makeText(getApplicationContext(), "wifi is disabled..making it enabled", Toast.LENGTH_LONG).show();
            wifi.setWifiEnabled(true);
        }
        this.adapter = new SimpleAdapter(WifiScanResult.this, arraylist, R.layout.list_item_wifi_scan_result, new String[]{ITEM_KEY}, new int[]{R.id.wifi_net_list});
        wifi_ssid_list.setAdapter(this.adapter);

        bReciver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context c, Intent intent) {
                wifiScanResults = wifi.getScanResults();
                wifiResultSize = wifiScanResults.size();
                String wifi_net_item;
                try {
                    wifiResultSize = wifiResultSize - 1;
                    while (wifiResultSize >= 0) {
                        HashMap<String, String> item = new HashMap<String, String>();
                        wifi_net_item = wifiScanResults.get(wifiResultSize).SSID + "  " + wifiScanResults.get(wifiResultSize).capabilities;
                        item.put(ITEM_KEY, wifi_net_item );
                        Log.i(TAG, "wifi net: " + wifi_net_item);

                        arraylist.add(item);
                        wifiResultSize--;
                        adapter.notifyDataSetChanged();
                    }
                } catch (Exception e) {
                }
            }
        };
        registerReceiver(bReciver, new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION));

    }

    public void onClick(View view) {
        arraylist.clear();
        wifi.startScan();
        try {
            wifiResultSize = wifiResultSize - 1;
            while (wifiResultSize >= 0) {
                HashMap<String, String> item = new HashMap<String, String>();
                item.put(ITEM_KEY, wifiScanResults.get(wifiResultSize).SSID + "  " + wifiScanResults.get(wifiResultSize).capabilities);

                arraylist.add(item);
                wifiResultSize--;
                adapter.notifyDataSetChanged();
            }
        } catch (Exception e) {
        }
        Toast.makeText(this, "Scanning...." + wifiResultSize, Toast.LENGTH_SHORT).show();

    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(bReciver);
    }
}
