package com.wyj.easyfloat.utils

import android.annotation.SuppressLint
import android.content.Context
import android.os.Handler
import android.os.Looper
import android.view.MotionEvent
import android.view.WindowManager
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import com.wyj.easyfloat.core.FloatingWindowManager

/**
 *  author : jie wang
 *  date : 2024/2/19 11:11
 *  description :
 */
object InputMethodUtils {

    @SuppressLint("ClickableViewAccessibility")
    internal fun initInputMethod(editText: EditText, tag: String? = null) {
        editText.setOnTouchListener { _, event ->
            if (event.action == MotionEvent.ACTION_DOWN) openInputMethod(editText, tag)
            false
        }
    }

    /**
     * 让浮窗获取焦点，并打开软键盘
     */
    @JvmStatic
    @JvmOverloads
    fun openInputMethod(editText: EditText, tag: String? = null) {
        FloatingWindowManager.getHelper(tag)?.apply {
            // 更改flags，并刷新布局，让系统浮窗获取焦点
            params.flags = WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL
            windowManager.updateViewLayout(frameLayout, params)
        }

        Handler(Looper.getMainLooper()).postDelayed({
            // 打开软键盘
            val inputManager =
                editText.context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager?
            inputManager?.showSoftInput(editText, 0)
        }, 100)
    }

    /**
     * 当软键盘关闭时，调用此方法，移除系统浮窗的焦点，不然系统返回键无效
     */
    @JvmStatic
    @JvmOverloads
    fun closedInputMethod(tag: String? = null) =
        FloatingWindowManager.getHelper(tag)?.run {
            params.flags =
                WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL or WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
            windowManager.updateViewLayout(frameLayout, params)
        }

}