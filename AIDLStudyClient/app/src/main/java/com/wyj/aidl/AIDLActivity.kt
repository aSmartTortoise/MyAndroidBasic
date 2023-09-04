package com.wyj.aidl

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.IBinder
import android.util.Log
import android.widget.Button
import android.widget.TextView
import com.wyj.aidl.client.MainActivity
import com.wyj.aidl.client.R
import com.wyj.aidl.server.IMsgManager
import com.wyj.aidl.server.IReceiveMsgListener
import com.wyj.aidl.server.Msg

class AIDLActivity : AppCompatActivity() {
    companion object {
        const val TAG = "AIDLActivity"
    }

    private var iMsgManager: IMsgManager? = null
    private var count = 0
    var tvDisplay: TextView? = null
    private val receiveMsgListener = object : IReceiveMsgListener.Stub() {
        override fun onReceive(msg: Msg?) {
            Log.d(TAG, "onReceive: wyj msg:$msg")
            runOnUiThread {
                tvDisplay?.text = msg?.msg
            }
        }
    }

    private val connection: ServiceConnection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            Log.d(TAG, "onServiceConnected: wyj name:$name")
            Log.d(
                TAG,
                "onServiceConnected: wyj service instance of IMsgManager.proxy:${service is IMsgManager.Stub}")
            iMsgManager = IMsgManager.Stub.asInterface(service).apply {
                asBinder().linkToDeath(deathRecipient, 0)
                registerReceiveListener(receiveMsgListener)
            }
        }

        override fun onServiceDisconnected(name: ComponentName?) {
            Log.d(TAG, "onServiceDisconnected: wyj name:$name")
        }

    }

    /**
     *  观察远端服务端进程的Binder是否退出，当退出后，注册DeathRecipient的客户端进程可以收到回调
     */
    private val deathRecipient = object : IBinder.DeathRecipient {
        override fun binderDied() {
            Log.d(TAG, "binderDied: wyj")
            iMsgManager?.let {
                it.asBinder().unlinkToDeath(this, 0)
                iMsgManager = null
            }
        }
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_aidlactivity)
        val btnBindService = findViewById<Button>(R.id.btn_bind_service)
        btnBindService.setOnClickListener {
            val intent = Intent().apply {
                action = "com.wyj.aidl.server.MyService"
                setPackage("com.wyj.aidl.server")
            }
            this@AIDLActivity.bindService(intent, connection, Context.BIND_AUTO_CREATE)
        }
        val btnSendMsg = findViewById<Button>(R.id.btn_send_msg)
        btnSendMsg.setOnClickListener {
            iMsgManager?.sendMsg(Msg("from 客户端，当前第 ${count++} 次", System.currentTimeMillis()))
        }
        tvDisplay = findViewById<TextView>(R.id.tv_display)
    }

    override fun onDestroy() {
        Log.d(TAG, "onDestroy: wyj")
        iMsgManager?.let {
            if (it.asBinder().isBinderAlive) {
                it.unregisterReceiveListener(receiveMsgListener)
            }
        }
        unbindService(connection)
        super.onDestroy()

    }

}