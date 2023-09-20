package com.wyj.performance

import android.content.BroadcastReceiver
import android.content.ComponentName
import android.content.Intent
import android.content.IntentFilter
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Process
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.wyj.performance.anr.AnrBroadcastReceiver
import com.wyj.performance.anr.AnrService
import com.wyj.performance.anr.InputEventTimeoutActivity
import com.wyj.performance.anr.StaticReceiver
import com.wyj.performance.memory.StaticPropertyActivity
import com.wyj.performance.memory.TestActivity

class MainActivity : AppCompatActivity() {
    companion object {
        const val TAG = "MainActivity"
    }
    var broadcastReceiver: BroadcastReceiver? = null
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

        findViewById<View>(R.id.btn_send_ordered_broadcast).setOnClickListener {
            val intent = Intent().apply {
                action = "anr"
                addFlags(Intent.FLAG_RECEIVER_FOREGROUND)
            }
            sendOrderedBroadcast(intent, null)
        }

        findViewById<View>(R.id.btn_send_broadcast).setOnClickListener {
            Log.d(TAG, "onCreate: wyj send not ordered broadcast.")
            val intent = Intent().apply {
                addFlags(Intent.FLAG_RECEIVER_FOREGROUND)
                action = "anr"
                // Android 8 以后，静态注册的广播接收器如果希望能收到自定义广播，发送广播的意图需要是显示的。
                // https://blog.csdn.net/u012894808/article/details/88870765
                if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.O) {

                } else {
                    component = ComponentName(this@MainActivity, StaticReceiver::class.java)
                }
            }
            sendBroadcast(intent)
        }

        findViewById<View>(R.id.btn_start_service).setOnClickListener {
            Log.d(TAG, "onCreate: start service")
            startService(Intent(this@MainActivity, AnrService::class.java))
        }

        findViewById<View>(R.id.btn_provider_query).setOnClickListener {
            val uri: Uri = Uri.parse("content://com.gityuan.articles/android/3")
            contentResolver.query(uri, null, null, null, null)
        }

        registerReceiver()
    }

    /**
     *  动态注册的广播，当收到前台广播后，广播接收器onReceive方法耗时超过5s，在期间点击返回键，也会anr，不一定会
     *  弹anr的弹窗，会有trace日志。
     */
    private fun registerReceiver() {
        if (broadcastReceiver == null) {
            broadcastReceiver  = AnrBroadcastReceiver()
            val intentFilter = IntentFilter().apply {
                addAction("anr")
            }

            Log.d(TAG, "registerReceiver: ")
            registerReceiver(broadcastReceiver, intentFilter)
        }

    }

    override fun onDestroy() {
        super.onDestroy()
        broadcastReceiver?.let {
            unregisterReceiver(it)
            broadcastReceiver = null
        }
    }
}