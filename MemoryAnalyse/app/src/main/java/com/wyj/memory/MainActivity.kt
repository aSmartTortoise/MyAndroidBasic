package com.wyj.memory

import android.content.Intent
import android.os.Bundle
import android.os.Process
import android.view.View
import androidx.appcompat.app.AppCompatActivity

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
    }
}