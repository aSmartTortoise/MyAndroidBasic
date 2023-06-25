package com.wyj.viewsysterm

import android.annotation.SuppressLint
import android.content.Context
import android.util.AttributeSet
import android.util.Log
import android.view.MotionEvent
import android.view.ViewGroup
import androidx.constraintlayout.widget.ConstraintLayout

class TestConstraintLayout : ConstraintLayout {
    companion object {
        const val TAG = "TestConstraintLayout"
    }
    constructor(ctx: Context) : super(ctx)
    constructor(ctx: Context, attrs: AttributeSet) : super(ctx, attrs)
    constructor(ctx: Context, attrs: AttributeSet, defStyleAttr: Int): super(ctx, attrs, defStyleAttr)

    @SuppressLint("DiscouragedPrivateApi")
    override fun dispatchTouchEvent(ev: MotionEvent?): Boolean {
        Log.d(TAG, "dispatchTouchEvent: action:${ev?.action}")
        val result = super.dispatchTouchEvent(ev)
        Log.d(TAG, "dispatchTouchEvent: result:$result")
        val firstTouchTarget = ViewGroup::class.java.getDeclaredField("mFirstTouchTarget")
        Log.d(TAG, "dispatchTouchEvent: wyj firstTouchTarget:$firstTouchTarget")
        return result
    }

    override fun onInterceptTouchEvent(ev: MotionEvent?): Boolean {
        Log.d(TAG, "onInterceptTouchEvent: wyj action:${ev?.action}")
        return super.onInterceptTouchEvent(ev)
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent?): Boolean {
        Log.d(TAG, "onTouchEvent: action:${event?.action}")
        return super.onTouchEvent(event)
    }
}