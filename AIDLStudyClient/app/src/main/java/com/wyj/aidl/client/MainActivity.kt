package com.wyj.aidl.client

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.IBinder
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.TextView
import com.wyj.aidl.AIDLActivity
import com.wyj.aidl.server.IMsgManager
import com.wyj.aidl.server.IReceiveMsgListener
import com.wyj.aidl.server.Msg

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        findViewById<View>(R.id.btn_aidl).setOnClickListener {
            Intent(this, AIDLActivity::class.java).apply {
                startActivity(this)
            }
        }
    }

}