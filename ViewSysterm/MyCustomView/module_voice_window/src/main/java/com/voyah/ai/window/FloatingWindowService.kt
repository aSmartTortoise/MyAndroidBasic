package com.voyah.ai.window

import android.app.Service
import android.content.Intent
import android.os.IBinder
import android.util.Log
import android.view.Gravity
import android.view.View
import android.widget.Toast
import com.voyah.easyfloat.EasyFloat
import com.voyah.easyfloat.ShowPattern
import com.voyah.easyfloat.SidePattern
import com.voyah.easyfloat.`interface`.OnInvokeView
import com.voyah.easyfloat.utils.DisplayUtils

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
        showFloatWindow()
        return super.onStartCommand(intent, flags, startId)
    }

   private fun showFloatWindow() {
       //        EasyWindow.with(application)
//            .setContentView(R.layout.window_hint)
//            .addWindowFlags(WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH)
//            .setImageDrawable(android.R.id.icon, R.drawable.ic_dialog_tip_finish)
//            .setText(android.R.id.message, "hello WindowManager")
//            .show()
       val statusBarHeight = DisplayUtils.getStatusBarHeight(this)
       EasyFloat.with(application)
            .setShowPattern(ShowPattern.ALL_TIME)
            .setSidePattern(SidePattern.DEFAULT)
            .setImmersionStatusBar(true)
            .setDragEnable(true)
            .setGravity(Gravity.END or Gravity.BOTTOM, 0, statusBarHeight)
            .setLayout(R.layout.window_hint, object : OnInvokeView {
                override fun invoke(view: View) {
                    Log.d(TAG, "invoke: view:$view")
                    view.findViewById<View>(R.id.tv_toast).setOnClickListener {
                        Toast.makeText(this@FloatingWindowService, "嘿哈", Toast.LENGTH_LONG).show()
                    }

                }

            })
            .registerCallback {
                Log.d(TAG, "showFloatWindow: createResult")
                createResult { b, s, view ->
                    Log.d(TAG, "createResult action: b:$b, s:$s")
                }

                show {
                    Log.d(TAG, "show 悬浮窗。")
                }

                hide {
                    Log.d(TAG, "hide 悬浮窗。")
                }

                dismiss {
                    Log.d(TAG, "dismiss 悬浮窗。")
                }

                outsideTouch { view, motionEvent ->
                    Log.d(TAG, "outsideTouch，点击到窗体区域外。")
                }
            }
            .show()
    }


}