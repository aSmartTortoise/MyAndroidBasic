package com.wyj.motionevent

import android.annotation.SuppressLint
import android.content.Context
import android.util.AttributeSet
import android.util.Log
import android.view.MotionEvent
import androidx.appcompat.widget.AppCompatButton

class TestButton @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null
) : AppCompatButton(context, attrs) {
    companion object {
        const val TAG = "TestButton"
    }

    @SuppressLint("SoonBlockedPrivateApi")
    override fun dispatchTouchEvent(event: MotionEvent?): Boolean {
        Log.d(TAG, "dispatchTouchEvent: action:${event?.action}")
        return super.dispatchTouchEvent(event)
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent?): Boolean {
        val result = super.onTouchEvent(event)
        Log.d(TAG, "onTouchEvent: action:${event?.action}, result:$result")
        return result
    }

}