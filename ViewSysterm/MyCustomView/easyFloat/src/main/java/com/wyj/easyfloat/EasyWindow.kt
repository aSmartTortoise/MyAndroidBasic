package com.wyj.easyfloat

import android.R
import android.app.Activity
import android.app.Application
import android.content.Context
import android.graphics.PixelFormat
import android.graphics.drawable.Drawable
import android.os.Build
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.*
import android.view.ViewGroup.MarginLayoutParams
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import com.wyj.easyfloat.view.WindowDecorView

class EasyWindow private constructor(val context: Context) {

    private var windowManager: WindowManager? = null
    private var windowParams: WindowManager.LayoutParams? = null
    private var decorView: ViewGroup? = null
    var showing: Boolean = false
    private var lifecycleCallback: WindowLifecycleCallback? = null
    private val updateRunnable = Runnable { update() }

    companion object {
        const val TAG = "EasyWindow"
        val HANDLER: Handler = Handler(Looper.getMainLooper())
        fun with(application: Application): EasyWindow {
            return EasyWindow(application)
        }
    }

    init {
        Log.d(TAG, "init")
        decorView = WindowDecorView(context)
        windowManager = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
        windowParams = WindowManager.LayoutParams().apply {
            width = WindowManager.LayoutParams.WRAP_CONTENT
            height = WindowManager.LayoutParams.WRAP_CONTENT
            format = PixelFormat.TRANSLUCENT
            windowAnimations = R.style.Animation_Toast
            packageName = context.packageName
            // 设置触摸外层布局（除 WindowManager 外的布局，默认是 WindowManager 显示的时候外层不可触摸）
            // 需要注意的是设置了 FLAG_NOT_TOUCH_MODAL 必须要设置 FLAG_NOT_FOCUSABLE，否则就会导致用户按返回键无效
            // 设置触摸外层布局（除 WindowManager 外的布局，默认是 WindowManager 显示的时候外层不可触摸）
            // 需要注意的是设置了 FLAG_NOT_TOUCH_MODAL 必须要设置 FLAG_NOT_FOCUSABLE，否则就会导致用户按返回键无效
            flags = (WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL
                    or WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE)
        }
    }

    private constructor(application: Application) : this(application as Context) {
        Log.d(TAG, "constructor setWindowType sdk version:${Build.VERSION.SDK_INT}")
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            setWindowType(WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY)
        }
    }

    /**
     * 设置内容布局
     */
    fun setContentView(id: Int): EasyWindow {
        return setContentView(LayoutInflater.from(context).inflate(id, decorView, false))
    }

    fun setContentView(view: View): EasyWindow {
        decorView?.let {
            if (it.childCount > 0) {
                it.removeAllViews()
            }
            it.addView(view)
        }

        val layoutParams = view.layoutParams
        Log.d(TAG, "setContentView: layoutParams:$layoutParams")
        if (layoutParams is MarginLayoutParams) {
            // 清除 Margin，因为 WindowManager 没有这一属性可以设置，并且会跟根布局相冲突
            layoutParams.topMargin = 0
            layoutParams.bottomMargin = 0
            layoutParams.leftMargin = 0
            layoutParams.rightMargin = 0
        }

        // 如果当前没有设置重心，就自动获取布局重心
        windowParams?.let {
            if (it.gravity == Gravity.NO_GRAVITY) {
                if (layoutParams is FrameLayout.LayoutParams) {
                    val gravity = layoutParams.gravity
                    if (gravity != FrameLayout.LayoutParams.UNSPECIFIED_GRAVITY) {
                        it.gravity = gravity
                    }
                } else if (layoutParams is LinearLayout.LayoutParams) {
                    val gravity = layoutParams.gravity
                    if (gravity != FrameLayout.LayoutParams.UNSPECIFIED_GRAVITY) {
                        it.gravity = gravity
                    }
                }
                if (it.gravity == Gravity.NO_GRAVITY) {
                    // 默认重心是居中
                    it.gravity = Gravity.CENTER
                }
            }
            if (layoutParams != null) {
                if (it.width == WindowManager.LayoutParams.WRAP_CONTENT &&
                    it.height == WindowManager.LayoutParams.WRAP_CONTENT) {
                    // 如果当前 Dialog 的宽高设置了自适应，就以布局中设置的宽高为主
                    it.width = layoutParams.width
                    it.height = layoutParams.height
                } else {
                    // 如果当前通过代码动态设置了宽高，则以动态设置的为主
                    layoutParams.width = it.width
                    layoutParams.height = it.height
                }
            }
        }

        postUpdate()
        return this
    }

    fun setAnimStyle(animId: Int): EasyWindow {
        windowParams?.windowAnimations = animId
        postUpdate()
        return this
    }

    fun addWindowFlags(flag: Int): EasyWindow {
        windowParams?.flags = windowParams?.flags?.or(flag)
        postUpdate()
        return this
    }

    fun setImageDrawable(viewId: Int, drawableId: Int): EasyWindow {
        val drawable = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            context.getDrawable(drawableId)
        } else {
            context.resources.getDrawable(drawableId)
        }
        return setImageDrawable(viewId, drawable)
    }

    fun setImageDrawable(viewId: Int, drawable: Drawable?): EasyWindow {
        (findViewById<View>(viewId) as ImageView).setImageDrawable(drawable)
        return this
    }

    private fun <V : View?> findViewById(id: Int): V? {
        return decorView?.findViewById<V>(id)
    }

    fun setText(id: Int, text: CharSequence?): EasyWindow {
        findViewById<View>(id)?.let { view ->
            if (view is TextView) {
                view.text = text
            }
        }
        return this
    }

    fun show() {
        require(!(decorView?.childCount == 0 || windowParams == null)) {
            "WindowParams and view cannot be empty"
        }
        if (showing) {
            update()
            return
        }
        if (context is Activity) {
            val activity = context
            if (activity.isFinishing || activity.isDestroyed) {
                return
            }
        }

        try {
            // 如果 View 已经被添加的情况下，就先把 View 移除掉
            decorView?.let {
                if (it.parent != null) {
                    windowManager?.removeViewImmediate(it)
                }
                windowManager?.addView(it, windowParams)
            }

            // 当前已经显示
            showing = true
            // 如果当前限定了显示时长
//        if (mDuration != 0) {
//            removeCallbacks(this)
//            postDelayed(this, mDuration.toLong())
//        }

            lifecycleCallback?.onWindowShow(this)
        } catch (e: NullPointerException ) {
            e.printStackTrace()
        } catch (e: IllegalStateException) {
            // 如果这个 View 对象被重复添加到 WindowManager 则会抛出异常
            // java.lang.IllegalStateException: View has already been added to the window manager.
            e.printStackTrace()
        } catch (e: IllegalArgumentException) {
            e.printStackTrace()
        } catch (e: WindowManager.BadTokenException) {
            e.printStackTrace()
        }
    }

    private fun setWindowType(windowType: Int) {
        windowParams?.type = windowType
        postUpdate()
    }

    private fun postUpdate() {
        if (!showing) {
            return
        }
        removeCallbacks(updateRunnable)
        postDelayed(updateRunnable)
    }

    private fun removeCallbacks(runnable: Runnable) {
        HANDLER.removeCallbacks(runnable)
    }

    private fun postDelayed(runnable: Runnable, delayMillis: Long = 0L) {
        var delayTime = delayMillis
        if (delayMillis < 0) {
            delayTime = 0
        }
        HANDLER.postDelayed(runnable, delayTime)
    }

    private fun update() {
        if (!showing) {
            return
        }
        try {
            // 更新 WindowManger 的显示
            windowManager?.updateViewLayout(decorView, windowParams)
            lifecycleCallback?.onWindowUpdate(this)
        } catch (e: IllegalArgumentException) {
            // 当 WindowManager 已经消失时调用会发生崩溃
            // IllegalArgumentException: View not attached to window manager
            e.printStackTrace()
        }
    }

    interface WindowLifecycleCallback {
        /**
         * 窗口显示回调
         */
        fun onWindowShow(easyWindow: EasyWindow)

        /**
         * 窗口更新回调
         */
        fun onWindowUpdate(easyWindow: EasyWindow)

        /**
         * 窗口消失回调
         */
        fun onWindowCancel(easyWindow: EasyWindow)

        /**
         * 窗口回收回调
         */
        fun onWindowRecycle(easyWindow: EasyWindow)

        /**
         * 窗口可见性发生变化
         */
        fun onWindowVisibilityChanged(easyWindow: EasyWindow, visibility: Int)
    }

}