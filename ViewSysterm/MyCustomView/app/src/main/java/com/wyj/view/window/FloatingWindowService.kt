package com.wyj.view.window

import android.app.Service
import android.content.Intent
import android.os.IBinder
import android.util.Log
import android.view.Gravity
import android.view.View
import android.view.WindowManager
import com.wyj.easyfloat.EasyFloat
import com.wyj.easyfloat.EasyWindow
import com.wyj.easyfloat.ShowPattern
import com.wyj.easyfloat.SidePattern
import com.wyj.easyfloat.`interface`.OnInvokeView
import com.wyj.view.R

class FloatingWindowService : Service() {

    companion object {
        const val TAG = "FloatingWindowService"
    }

    override fun onCreate() {
        super.onCreate()
        Log.d(TAG, "onCreate")
    }

    override fun onBind(intent: Intent): IBinder? {
        return null
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Log.d(TAG, "onStartCommand: show window")
//        EasyWindow.with(application)
//            .setContentView(R.layout.window_hint)
//            .addWindowFlags(WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH)
//            .setImageDrawable(android.R.id.icon, R.drawable.ic_dialog_tip_finish)
//            .setText(android.R.id.message, "hello WindowManager")
//            .show()
        showFloatWindow()
        return super.onStartCommand(intent, flags, startId)
    }

    fun showFloatWindow() {
        EasyFloat.with(application)
            .setShowPattern(ShowPattern.ALL_TIME)
            .setSidePattern(SidePattern.AUTO_SIDE)
            .setImmersionStatusBar(true)
            .setGravity(Gravity.START, 0, 10)
            .setLayout(R.layout.window_hint, object : OnInvokeView {
                override fun invoke(view: View) {
                    Log.d(TAG, "invoke: view:$view")
                }

            })
            .registerCallback {
                createResult { b, s, view ->
                    Log.d(TAG, "showFloatWindow: b:$b, s:$s")
                }
            }
            .show()
    }


}