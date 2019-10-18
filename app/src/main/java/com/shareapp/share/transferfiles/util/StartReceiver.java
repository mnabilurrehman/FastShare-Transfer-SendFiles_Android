package com.shareapp.share.transferfiles.util;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.shareapp.share.transferfiles.transfer.TransferService;

/**
 * Starts the transfer service
 */
public class StartReceiver extends BroadcastReceiver {

    private final static String TAG = "AppLog/StartReceiver";

    @Override
    public void onReceive(Context context, Intent intent) {
        android.util.Log.d(TAG, "Starting Service...");
        if (new Settings(context).getBoolean(Settings.Key.BEHAVIOR_RECEIVE)) {
            TransferService.startStopService(context, true);
        }
    }

}
