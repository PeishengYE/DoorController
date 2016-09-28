package com.radioyps.doorcontroller;

import android.app.IntentService;
import android.content.Intent;

/**
 * Created by developer on 28/09/16.
 */
public class PingControllerService extends IntentService {
    public PingControllerService() {
        super("com.radioyps.doorcontroller");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        String action = intent.getAction();

        if(action.equals(CommonConstants.ACTION_PING)) {
           // issueNotification(intent, mMessage);
        }
    }
}
