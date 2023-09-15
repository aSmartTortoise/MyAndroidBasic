package com.wyj.performance.anr;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.SystemClock;
import android.util.Log;

public class AnrBroadcastReceiver extends BroadcastReceiver {
    private static final String TAG = "AnrReceiver";
    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d(TAG, "onReceive: wyj action:" + intent.getAction());
//        SystemClock.sleep(10_100);
//        Log.d(TAG, "onReceive: sleep end.");
    }
}
