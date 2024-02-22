package com.voyah.easyfloat.view

import android.annotation.SuppressLint
import android.content.Context
import android.util.AttributeSet
import android.util.Log
import android.view.KeyEvent
import android.view.MotionEvent
import android.widget.FrameLayout
import com.voyah.easyfloat.FloatConfig
import com.voyah.easyfloat.`interface`.OnFloatTouchListener
import com.voyah.easyfloat.utils.InputMethodUtils

/**
 *  author : jie wang
 *  date : 2024/2/19 11:09
 *  description : 系统浮窗的父布局，对touch事件进行了重新分发
 */

@SuppressLint("ViewConstructor")
internal class ParentFrameLayout @JvmOverloads constructor(
    private val config: FloatConfig,
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr) {

    companion object {
        const val TAG = "ParentFrameLayout"
    }

    var touchListener: OnFloatTouchListener? = null
    var layoutListener: OnLayoutListener? = null
    private var isCreated = false

    // 布局绘制完成的接口，用于通知外部做一些View操作，不然无法获取view宽高
    interface OnLayoutListener {
        fun onLayout()
    }

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        super.onLayout(changed, left, top, right, bottom)
        // 初次绘制完成的时候，需要设置对齐方式、坐标偏移量、入场动画
        if (!isCreated) {
            isCreated = true
            layoutListener?.onLayout()
        }
    }

    override fun dispatchTouchEvent(ev: MotionEvent?): Boolean {
        val actionMasked = ev?.action?.and(MotionEvent.ACTION_MASK)
        Log.d(WindowDecorView.TAG, "dispatchTouchEvent: actionMasked:$actionMasked")
        if (actionMasked == MotionEvent.ACTION_OUTSIDE) {
            Log.d(WindowDecorView.TAG, "dispatchTouchEvent: 点击到外部区域了~")

        }
        return super.dispatchTouchEvent(ev)
    }

    override fun onInterceptTouchEvent(event: MotionEvent?): Boolean {
        if (event != null) touchListener?.onTouch(event)
        // 是拖拽事件就进行拦截，反之不拦截
        // ps：拦截后将不再回调该方法，会交给该view的onTouchEvent进行处理，所以后续事件需要在onTouchEvent中回调
        return config.isDrag || super.onInterceptTouchEvent(event)
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent?): Boolean {
        if (event != null) touchListener?.onTouch(event)
        return config.isDrag || super.onTouchEvent(event)
    }

    /**
     * 按键转发到视图的分发方法，在这里关闭输入法
     */
    override fun dispatchKeyEventPreIme(event: KeyEvent?): Boolean {
        if (config.hasEditText && event?.action == KeyEvent.ACTION_DOWN && event.keyCode == KeyEvent.KEYCODE_BACK) {
            InputMethodUtils.closedInputMethod(config.floatTag)
        }
        return super.dispatchKeyEventPreIme(event)
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        config.callback?.dismiss()
        config.floatCallback?.builder?.dismiss?.invoke()
    }
}