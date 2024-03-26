package com.voyah.window

import android.app.Service
import android.content.Intent
import android.os.IBinder
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.Gravity
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import com.voyah.window.R
import com.voyah.easyfloat.EasyFloat
import com.voyah.easyfloat.ShowPattern
import com.voyah.easyfloat.SidePattern
import com.voyah.easyfloat.`interface`.OnInvokeView
import com.voyah.easyfloat.utils.DisplayUtils

class FloatingWindowService : Service() {

    companion object {
        const val TAG = "FloatingWindowService"
    }

    private var screenWidth: Lazy<Int> = lazy {
        DisplayUtils.getScreenWidth(this)
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
       val statusBarHeight = DisplayUtils.getStatusBarHeight(this)
       val dp620 = resources.getDimensionPixelSize(R.dimen.dp_620)
       val dp230 = resources.getDimensionPixelSize(R.dimen.dp_230)
       Log.d(TAG, "showFloatWindow: dp620:$dp620")
       val x = (screenWidth.value - dp230) / 2
       EasyFloat.with(application)
            .setShowPattern(ShowPattern.ALL_TIME)
            .setImmersionStatusBar(true)
            .setDragEnable(false)
            .setLocation(x, 0)
            .setLayout(R.layout.window_vpa_typewriter_card2, object : OnInvokeView {
                override fun invoke(view: View) {
                    Log.d(TAG, "invoke: view:$view")
                    val llContent = view.findViewById<LinearLayout>(R.id.ll_content)
                    Log.d(TAG, "invoke: llContent width:${llContent.width}")
                    val tvText = view.findViewById<TextView>(R.id.tv_text).apply {
                        addTextChangedListener(object: TextWatcher {
                            override fun beforeTextChanged(
                                s: CharSequence?,
                                start: Int,
                                count: Int,
                                after: Int
                            ) {
                            }

                            override fun onTextChanged(
                                s: CharSequence?,
                                start: Int,
                                before: Int,
                                count: Int
                            ) {
                                llContent.post {
                                    Log.d(TAG, "onTextChanged: llContentWidth:${llContent.width}")
                                }

                            }

                            override fun afterTextChanged(s: Editable?) {
                            }

                        })
                    }

                    tvText.postDelayed(Runnable {
                        tvText.text = getString(R.string.typewriterLong)
                    }, 1000L)
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