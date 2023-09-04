package com.wyj.memory

import android.os.Bundle
import android.os.Handler
import android.os.Message
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import java.lang.ref.WeakReference

class TestActivity : AppCompatActivity() {
    companion object {
        private const val COUNT_TIME_MESSAGE = 1000
    }
    private var textView: TextView? = null
    private var timer = 0
    private var mHandler: CountTimeHandler? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_test)
        textView = findViewById<TextView>(R.id.text)
        mHandler = CountTimeHandler(WeakReference(this))
        mHandler?.sendEmptyMessage(COUNT_TIME_MESSAGE)
//        mHandler?.post(object : Runnable {
//            override fun run() {
//                textView!!.text = timer++.toString()
//                mHandler?.postDelayed(this, 1000L)
//            }
//        })
    }

    private fun updateTime() {
        textView!!.text = timer++.toString()
        mHandler?.sendEmptyMessageDelayed(COUNT_TIME_MESSAGE, 1000L)
    }


    class CountTimeHandler(private val mReference: WeakReference<TestActivity>) :
        Handler() {
        override fun handleMessage(msg: Message) {
            super.handleMessage(msg)
            if (msg.what == COUNT_TIME_MESSAGE) {
                val activity = mReference.get()
                if (activity == null || activity.isFinishing) return
                activity.updateTime()
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        mHandler?.let {
            it.removeCallbacksAndMessages(null)
            mHandler = null
        }
    }
}