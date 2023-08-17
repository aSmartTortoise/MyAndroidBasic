package com.wyj.motionevent

import android.annotation.SuppressLint
import android.content.Context
import android.util.AttributeSet
import android.util.Log
import android.view.MotionEvent

class TestButton : androidx.appcompat.widget.AppCompatButton {
    companion object {
        const val TAG = "TestButton"
    }
    constructor(ctx: Context) : super(ctx)
    constructor(ctx: Context, attrs: AttributeSet) : super(ctx, attrs)
    constructor(ctx: Context, attrs: AttributeSet, defStyleAttr: Int)
            : super(ctx, attrs, defStyleAttr)

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