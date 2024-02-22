package com.voyah.easyfloat.`interface`

import android.view.MotionEvent

/**
 *  author : jie wang
 *  date : 2024/2/19 11:10
 *  description : 系统浮窗的触摸事件
 */
internal interface OnFloatTouchListener {

    fun onTouch(event: MotionEvent)
}