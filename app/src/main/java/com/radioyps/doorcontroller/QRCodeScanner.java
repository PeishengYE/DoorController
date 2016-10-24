package com.radioyps.doorcontroller;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.widget.Toast;

import com.google.zxing.Result;

import me.dm7.barcodescanner.zxing.ZXingScannerView;

/**
 * Created by developer on 24/10/16.
 */
public class QRCodeScanner extends FragmentActivity implements ZXingScannerView.ResultHandler {
    private ZXingScannerView mScannerView;
    private String mScanningContent = null;
    private String mScanningFormat = null;
    private ZXingScannerView.ResultHandler resultHandler = null;
    private static final String TAG = MainActivity.class.getName();



    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        mScannerView = new ZXingScannerView(this);   // Programmatically initialize the scanner view
        setContentView(mScannerView);
        if(savedInstanceState == null){
            Log.v(TAG, "onCreate()>> first created " );
            // Set the scanner view as the content view
        }else {
                /*
                not called, during dialog fragment showing onscreen,
                and user click the back button
                 in fact, this crashed the app
                */
            Log.v(TAG, "onCreate()>> I'm back " );
            //mScannerView.resumeCameraPreview(resultHandler);
        }

    }

    @Override
    public void onResume() {
        super.onResume();
        Log.v(TAG, "onResume()>> " ); // Prints scan results

        mScannerView.setResultHandler(this); // Register ourselves as a handler for scan results.
        mScannerView.startCamera();// Start camera on resume
    }

    @Override
    public void onPause() {
        super.onPause();
        Log.v(TAG, "onPause()>> " );
        mScannerView.stopCamera();           // Stop camera on pause
    }

    @Override
    public void handleResult(Result rawResult) {
        // Do something with the result here
        mScanningFormat = rawResult.getBarcodeFormat().toString();
        mScanningContent  = rawResult.getText();
        String token = mScanningContent;
        Log.v(TAG, "QR code: " + rawResult.getText()); // Prints scan results
        Log.v(TAG, "QR Format: " + rawResult.getBarcodeFormat().toString()); // Prints the scan format (qrcode, pdf417 etc.)
        Utils.saveToken(token, this);
        Toast.makeText(this, R.string.QRcode_scan_success, Toast.LENGTH_LONG).show();
        resultHandler = this;
        finish();

    }



}
