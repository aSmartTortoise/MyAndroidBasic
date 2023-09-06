package com.wyj.performance

import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.os.Process
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.wyj.performance.anr.AnrBroadcastReceiver
import com.wyj.performance.anr.InputEventTimeoutActivity
import com.wyj.performance.memory.StaticPropertyActivity
import com.wyj.performance.memory.TestActivity

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val pid = Process.myPid()
        println("onCreate, pid:$pid")
        findViewById<View>(R.id.btn_handler).setOnClickListener(View.OnClickListener {
            startActivity(
                Intent(this@MainActivity, TestActivity::class.java)
            )
        })

        findViewById<View>(R.id.btn_static_property).setOnClickListener(View.OnClickListener {
            startActivity(
                Intent(this@MainActivity, StaticPropertyActivity::class.java)
            )
        })

        findViewById<View>(R.id.btn_input_event).setOnClickListener(View.OnClickListener {
            startActivity(
                Intent(this@MainActivity, InputEventTimeoutActivity::class.java)
            )
        })

        findViewById<View>(R.id.btn_send_broadcast).setOnClickListener {
            val intent = Intent().apply {
                action = "anr"
                addFlags(Intent.FLAG_RECEIVER_FOREGROUND)
            }
            sendBroadcast(intent)
        }

        registerReceiver()
    }

    /**
     *  动态注册的广播，当收到前台广播后，广播接收器onReceive方法耗时超过5s，在期间点击返回键，也会anr，不一定会
     *  弹anr的弹窗，会又trace日志。
     */
    private fun registerReceiver() {
        val broadcastReceiver = AnrBroadcastReceiver()
        val intentFilter = IntentFilter().apply {
            addAction("anr")
        }

        registerReceiver(broadcastReceiver, intentFilter)
    }
}