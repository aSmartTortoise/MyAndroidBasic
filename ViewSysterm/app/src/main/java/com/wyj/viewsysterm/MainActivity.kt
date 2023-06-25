package com.wyj.viewsysterm

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.widget.ActionBarContextView
import androidx.appcompat.widget.FitWindowsLinearLayout
import androidx.constraintlayout.widget.ConstraintLayout

class MainActivity : AppCompatActivity(), View.OnTouchListener, View.OnClickListener {
    companion object {
        const val TAG = "MainActivity"
    }
    @SuppressLint("MissingInflatedId", "ClickableViewAccessibility")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
//        setContentView(androidx.appcompat.R.layout.abc_screen_simple)
        val btn = findViewById<Button>(R.id.btn_click)
        findViewById<TestButton>(R.id.test_btn).apply {
            setOnTouchListener(this@MainActivity)
            setOnClickListener(this@MainActivity)
        }
        val clContent = findViewById<ConstraintLayout>(R.id.test_cl_content)
        btn.setOnTouchListener(this)
        btn.setOnClickListener(this)
        clContent.setOnTouchListener(this)
        clContent.setOnClickListener(this)

    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouch(v: View?, event: MotionEvent?): Boolean {
        Log.d(TAG, "OnTouchListener--onTouch-- action= ${event?.action} -- $v");
        return false
    }

    override fun onClick(v: View?) {
        Log.d(TAG, "OnClickListener--onClick-- $v");
    }

    override fun dispatchTouchEvent(ev: MotionEvent?): Boolean {
        Log.d(TAG, "dispatchTouchEvent: action:${ev?.action}")
        return super.dispatchTouchEvent(ev)
    }

    override fun onUserInteraction() {
        Log.d(TAG, "onUserInteraction:")
        super.onUserInteraction()
    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        Log.d(TAG, "onTouchEvent: action:${event?.action}")
        return super.onTouchEvent(event)
    }
}