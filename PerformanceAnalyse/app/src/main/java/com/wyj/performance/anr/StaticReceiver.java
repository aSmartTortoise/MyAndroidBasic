package com.wyj.performance.anr;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.SystemClock;
import android.util.Log;

/**
 *  Android O 8，静态注册的广播接收器接收不了自定义广播了
 *  https://blog.csdn.net/u012894808/article/details/88870765
 */
public class StaticReceiver extends BroadcastReceiver {
    private static final String TAG = "StaticReceiver";

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d(TAG, "onReceive: sleep start. action:" + intent.getAction());
        SystemClock.sleep(10_100);
        Log.d(TAG, "onReceive: sleep end.");
    }
}