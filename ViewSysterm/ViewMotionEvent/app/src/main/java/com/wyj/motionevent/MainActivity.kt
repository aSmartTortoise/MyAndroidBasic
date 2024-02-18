package com.wyj.motionevent

import android.annotation.SuppressLint
import android.app.Dialog
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.view.ViewConfiguration
import android.widget.Button
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
import com.wyj.motionevent.R

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
            Log.d(TAG, "onCreate: wyj clickable:${isClickable}, isLongClickable:$isLongClickable")
//            setOnClickListener(this@MainActivity)
        }
        findViewById<View>(R.id.v1).apply {
            setOnClickListener(this@MainActivity)
        }
        val clContent = findViewById<ConstraintLayout>(R.id.test_cl_content)
        btn.setOnTouchListener(this)
        btn.setOnClickListener(this)
        clContent.setOnTouchListener(this)
        clContent.setOnClickListener(this)
        val longPressTimeout = ViewConfiguration.getLongPressTimeout()
        val pressedStateDuration = ViewConfiguration.getPressedStateDuration()
        Log.d(TAG, "onCreate: longPressTimeout:$longPressTimeout, pressedStateDuration:$pressedStateDuration")
        Toast.makeText(this, "Hello, Toast", Toast.LENGTH_LONG).show()
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