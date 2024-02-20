package com.wyj.easyfloat.`interface`

import android.view.MotionEvent
import android.view.View

/**
 *  author : jie wang
 *  date : 2024/2/19 10:11
 *  description : 通过Kotlin DSL实现接口回调效果
 */
class FloatCallback {
    lateinit var builder: Builder

    // 带Builder接收者的高阶函数
    fun registerListener(block: Builder.() -> Unit) {
        this.builder = Builder().also(block)
    }

    inner class Builder {
        internal var createdResult: ((Boolean, String?, View?) -> Unit)? = null
        internal var show: ((View) -> Unit)? = null
        internal var hide: ((View) -> Unit)? = null
        internal var dismiss: (() -> Unit)? = null
        internal var touchEvent: ((View, MotionEvent) -> Unit)? = null
        internal var drag: ((View, MotionEvent) -> Unit)? = null
        internal var dragEnd: ((View) -> Unit)? = null
        internal var outsideTouch: ((View, MotionEvent) -> Unit)? = null

        fun createResult(action: (Boolean, String?, View?) -> Unit) {
            createdResult = action
        }

        fun show(action: (View) -> Unit) {
            show = action
        }

        fun hide(action: (View) -> Unit) {
            hide = action
        }

        fun dismiss(action: () -> Unit) {
            dismiss = action
        }

        fun touchEvent(action: (View, MotionEvent) -> Unit) {
            touchEvent = action
        }

        fun drag(action: (View, MotionEvent) -> Unit) {
            drag = action
        }

        fun dragEnd(action: (View) -> Unit) {
            dragEnd = action
        }

        fun outsideTouch(action: (View, MotionEvent) -> Unit) {
            outsideTouch = action
        }
    }
}