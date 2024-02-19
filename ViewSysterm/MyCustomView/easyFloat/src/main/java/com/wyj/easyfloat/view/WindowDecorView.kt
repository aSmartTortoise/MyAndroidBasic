package com.wyj.easyfloat.view

import android.content.Context
import android.util.AttributeSet
import android.util.Log
import android.view.MotionEvent
import android.widget.FrameLayout

class WindowDecorView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defaultStyleAttr: Int = 0
) : FrameLayout(context, attrs, defaultStyleAttr) {

    companion object {
        const val TAG = "WindowDecorView"
    }

    override fun dispatchTouchEvent(ev: MotionEvent?): Boolean {
        val actionMasked = ev?.action?.and(MotionEvent.ACTION_MASK)
        Log.d(TAG, "dispatchTouchEvent: actionMasked:$actionMasked")
        if (actionMasked == MotionEvent.ACTION_OUTSIDE) {
            Log.d(TAG, "dispatchTouchEvent: 点击到外部区域了~")
        }
        return super.dispatchTouchEvent(ev)
    }
}