package com.wyj.easyfloat.core

import android.animation.Animator
import android.annotation.SuppressLint
import android.app.Activity
import android.app.Service
import android.content.Context
import android.graphics.PixelFormat
import android.graphics.Rect
import android.os.Build
import android.os.IBinder
import android.util.Log
import android.view.*
import android.widget.EditText
import com.wyj.easyfloat.FloatConfig
import com.wyj.easyfloat.ShowPattern
import com.wyj.easyfloat.WARN_ACTIVITY_NULL
import com.wyj.easyfloat.`interface`.OnFloatTouchListener
import com.wyj.easyfloat.anim.AnimatorManager
import com.wyj.easyfloat.utils.DisplayUtils
import com.wyj.easyfloat.utils.InputMethodUtils
import com.wyj.easyfloat.utils.LifecycleUtils
import com.wyj.easyfloat.view.ParentFrameLayout

/**
 *  author : jie wang
 *  date : 2024/2/19 10:54
 *  description : 负责具体悬浮窗的创建管理
 */
internal class FloatingWindowHelper(val context: Context, var config: FloatConfig) {

    companion object {
        const val TAG = "FloatingWindowHelper"
    }

    lateinit var windowManager: WindowManager
    lateinit var windowParams: WindowManager.LayoutParams
    var decorView: ParentFrameLayout? = null
    private lateinit var touchUtils: TouchUtils
    private var enterAnimator: Animator? = null
    private var lastLayoutMeasureWidth = -1
    private var lastLayoutMeasureHeight = -1


    fun createWindow(callback: CreateCallback) {
        // 如果在onCreate创建单页面浮窗，会存在获取windowToken为空的情况，需要异步创建
        if (config.showPattern == ShowPattern.CURRENT_ACTIVITY && getToken() == null) {
            getActivity()?.findViewById<View>(android.R.id.content)?.run {
                post { callback.onCreate(createWindowInner()) }
                return
            }
            callback.onCreate(false)
            config.callback?.createdResult(false, WARN_ACTIVITY_NULL, null)
            config.floatCallback?.builder?.createdResult?.invoke(false, WARN_ACTIVITY_NULL, null)
        } else callback.onCreate(createWindowInner())
    }

    private fun createWindowInner(): Boolean = try {
        touchUtils = TouchUtils(context, config)
        initParams()
        addView()
        config.isShow = true
        true
    } catch (e: Exception) {
        config.callback?.createdResult(false, "$e", null)
        config.floatCallback?.builder?.createdResult?.invoke(false, "$e", null)
        false
    }

    private fun initParams() {
        windowManager = context.getSystemService(Service.WINDOW_SERVICE) as WindowManager
        windowParams = WindowManager.LayoutParams().apply {
            if (config.showPattern == ShowPattern.CURRENT_ACTIVITY) {
                // 设置窗口类型为应用子窗口，和PopupWindow同类型
                type = WindowManager.LayoutParams.TYPE_APPLICATION_PANEL
                // 子窗口必须和创建它的Activity的windowToken绑定
                token = getToken()
            } else {
                // 系统全局窗口，可覆盖在任何应用之上，以及单独显示在桌面上
                // 安卓6.0 以后，全局的Window类别，必须使用TYPE_APPLICATION_OVERLAY
                type = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
                else WindowManager.LayoutParams.TYPE_PHONE
            }
            format = PixelFormat.RGBA_8888
            gravity = Gravity.START or Gravity.TOP
            // 设置浮窗以外的触摸事件可以传递给后面的窗口、不自动获取焦点
            flags = if (config.immersionStatusBar)
            // 没有边界限制，允许窗口扩展到屏幕外
                (WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL
                        or WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
                        or WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS)
            else (WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL
                    or WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE)
            width = if (config.widthMatch) WindowManager.LayoutParams.MATCH_PARENT
                    else WindowManager.LayoutParams.WRAP_CONTENT
            height = if (config.heightMatch) WindowManager.LayoutParams.MATCH_PARENT
                    else WindowManager.LayoutParams.WRAP_CONTENT

            if (config.immersionStatusBar && config.heightMatch) {
                height = DisplayUtils.getScreenHeight(context)
            }

            // 如若设置了固定坐标，直接定位
            if (config.locationPair != Pair(0, 0)) {
                x = config.locationPair.first
                y = config.locationPair.second
            }
        }
    }


    private fun getActivity() =
        if (context is Activity) context else null

    private fun getToken(): IBinder? = getActivity()?.window?.decorView?.windowToken

    /**
     * 将自定义的布局，作为xml布局的父布局，添加到windowManager中，
     * 重写自定义布局的touch事件，实现拖拽效果。
     */
    private fun addView() {
        //浮窗父布局ParentFrameLayout与子view的context保持一致
        val rootViewContext = config.layoutView?.run { context } ?: context
        // 创建一个FrameLayout作为浮窗布局的父容器
        decorView = ParentFrameLayout(config, rootViewContext).apply {
            tag = config.floatTag
        }
        // 将浮窗布局文件添加到父容器FrameLayout中，并返回该浮窗文件
        val contentView = config.layoutView?.also { decorView?.addView(it) }
            ?: LayoutInflater.from(context).inflate(config.layoutId!!, decorView, true)
        // 为了避免创建的时候闪一下，我们先隐藏视图，不能直接设置GONE，否则定位会出现问题
        contentView.visibility = View.VISIBLE
        // 将decorView添加到系统windowManager中
        windowManager.addView(decorView, windowParams)

        // 通过重写decorView的Touch事件，实现拖拽效果
        decorView?.touchListener = object : OnFloatTouchListener {
            override fun onTouch(event: MotionEvent) =
                touchUtils.updateFloat(decorView!!, event, windowManager, windowParams)
        }

        // 在浮窗绘制完成的时候，设置初始坐标、执行入场动画
        decorView?.layoutListener = object : ParentFrameLayout.OnLayoutListener {
            override fun onLayout() {
                Log.d(TAG, "onLayout: setGravity")
                setGravity(decorView)
                lastLayoutMeasureWidth = decorView?.measuredWidth ?: -1
                lastLayoutMeasureHeight = decorView?.measuredHeight ?: -1
                Log.d(TAG, "onLayout lastLayoutMeasureWidth:$lastLayoutMeasureWidth, lastLayoutMeasureHeight:$lastLayoutMeasureHeight")
                config.apply {
                    // 如果设置了过滤当前页，或者后台显示前台创建、前台显示后台创建，隐藏浮窗，否则执行入场动画
                    if (filterSelf
                        || (showPattern == ShowPattern.BACKGROUND && LifecycleUtils.isForeground())
                        || (showPattern == ShowPattern.FOREGROUND && !LifecycleUtils.isForeground())
                    ) {
                        setVisible(View.GONE)
                        initEditText()
                    } else {
//                        enterAnim(contentView)
                    }

                    // 设置callbacks
                    layoutView = contentView
                    invokeView?.invoke(contentView)
                    callback?.createdResult(true, null, contentView)
                    floatCallback?.builder?.createdResult?.invoke(true, null, contentView)
                }
            }
        }

//        setChangedListener()
    }

    /**
     * 设置浮窗的对齐方式，支持上下左右、居中、上中、下中、左中和右中，默认左上角
     * 支持手动设置的偏移量
     */
    @SuppressLint("RtlHardcoded")
    private fun setGravity(view: View?) {
        if (config.locationPair != Pair(0, 0) || view == null) return
        val parentRect = Rect()
        // 获取浮窗所在的矩形
        windowManager.defaultDisplay.getRectSize(parentRect)
        val location = IntArray(2)
        // 获取View在整个屏幕内的绝对坐标
        view.getLocationOnScreen(location)
        // 通过绝对高度和相对高度比较，判断包含顶部状态栏
        val statusBarHeight = if (location[1] > windowParams.y) DisplayUtils.statusBarHeight(view)
                              else 0
        Log.d(TAG, "setGravity: location[1]:${location[1]}, windowParams.y:${windowParams.y}, " +
                "statusBarHeight:$statusBarHeight")
        val parentBottom =
            config.displayHeight.getDisplayRealHeight(context) - statusBarHeight
        Log.d(TAG, "setGravity: parentBottom:$parentBottom")
        when (config.gravity) {
            // 右上
            Gravity.END, Gravity.END or Gravity.TOP, Gravity.RIGHT, Gravity.RIGHT or Gravity.TOP ->
                windowParams.x = parentRect.right - view.width
            // 左下
            Gravity.START or Gravity.BOTTOM, Gravity.BOTTOM, Gravity.LEFT or Gravity.BOTTOM ->
                windowParams.y = parentBottom - view.height
            // 右下
            Gravity.END or Gravity.BOTTOM, Gravity.RIGHT or Gravity.BOTTOM -> {
                windowParams.x = parentRect.right - view.width
                windowParams.y = parentBottom - view.height
            }
            // 居中
            Gravity.CENTER -> {
                windowParams.x = (parentRect.right - view.width).shr(1)
                windowParams.y = (parentBottom - view.height).shr(1)
            }
            // 上中
            Gravity.CENTER_HORIZONTAL, Gravity.TOP or Gravity.CENTER_HORIZONTAL ->
                windowParams.x = (parentRect.right - view.width).shr(1)
            // 下中
            Gravity.BOTTOM or Gravity.CENTER_HORIZONTAL -> {
                windowParams.x = (parentRect.right - view.width).shr(1)
                windowParams.y = parentBottom - view.height
            }
            // 左中
            Gravity.CENTER_VERTICAL, Gravity.START or Gravity.CENTER_VERTICAL, Gravity.LEFT or Gravity.CENTER_VERTICAL ->
                windowParams.y = (parentBottom - view.height).shr(1)
            // 右中
            Gravity.END or Gravity.CENTER_VERTICAL, Gravity.RIGHT or Gravity.CENTER_VERTICAL -> {
                windowParams.x = parentRect.right - view.width
                windowParams.y = (parentBottom - view.height).shr(1)
            }
            // 其他情况，均视为左上
            else -> {
            }
        }

        // 设置偏移量
        windowParams.x += config.offsetPair.first
        windowParams.y += config.offsetPair.second

        if (config.immersionStatusBar) {
            if (config.showPattern != ShowPattern.CURRENT_ACTIVITY) {
//                windowParams.y -= statusBarHeight
            }
        } else {
            if (config.showPattern == ShowPattern.CURRENT_ACTIVITY) {
                windowParams.y += statusBarHeight
            }
        }
        // 更新浮窗位置信息
        windowManager.updateViewLayout(view, windowParams)
    }

    /**
     * 设置浮窗的可见性
     */
    fun setVisible(visible: Int, needShow: Boolean = true) {
        if (decorView == null || decorView!!.childCount < 1) return
        // 如果用户主动隐藏浮窗，则该值为false
        config.needShow = needShow
        decorView!!.visibility = visible
        val view = decorView!!.getChildAt(0)
        if (visible == View.VISIBLE) {
            config.isShow = true
            config.callback?.show(view)
            config.floatCallback?.builder?.show?.invoke(view)
        } else {
            config.isShow = false
            config.callback?.hide(view)
            config.floatCallback?.builder?.hide?.invoke(view)
        }
    }

    /**
     * 设置布局变化监听，根据变化时的对齐方式，设置浮窗位置
     */
    private fun setChangedListener() {
        decorView?.apply {
            // 监听DecorView布局完成
            viewTreeObserver?.addOnGlobalLayoutListener {
                Log.d(TAG, "onGlobalLayout")
                val filterInvalid = lastLayoutMeasureWidth == -1 || lastLayoutMeasureHeight == -1
                val filterEqual =
                    lastLayoutMeasureWidth == this.measuredWidth && lastLayoutMeasureHeight == this.measuredHeight
                Log.d(TAG, "setChangedListener: filterInvalid:$filterInvalid, filterEqual:$filterEqual")
                if (filterInvalid || filterEqual) {
                    return@addOnGlobalLayoutListener
                }

                // 水平方向
                if (config.layoutChangedGravity.and(Gravity.START) == Gravity.START) {
                    // ignore

                } else if (config.layoutChangedGravity.and(Gravity.END) == Gravity.END) {
                    val diffChangedSize = this.measuredWidth - lastLayoutMeasureWidth
                    windowParams.x = windowParams.x - diffChangedSize

                } else if (config.layoutChangedGravity.and(Gravity.CENTER_HORIZONTAL) == Gravity.CENTER_HORIZONTAL
                    || config.layoutChangedGravity.and(Gravity.CENTER) == Gravity.CENTER
                ) {
                    val diffChangedCenter = lastLayoutMeasureWidth / 2 - this.measuredWidth / 2
                    windowParams.x += diffChangedCenter
                }

                // 垂直方向
                if (config.layoutChangedGravity.and(Gravity.TOP) == Gravity.TOP) {
                    // ignore

                } else if (config.layoutChangedGravity.and(Gravity.BOTTOM) == Gravity.BOTTOM) {
                    val diffChangedSize = this.measuredHeight - lastLayoutMeasureHeight
                    windowParams.y = windowParams.y - diffChangedSize

                } else if (config.layoutChangedGravity.and(Gravity.CENTER_VERTICAL) == Gravity.CENTER_VERTICAL
                    || config.layoutChangedGravity.and(Gravity.CENTER) == Gravity.CENTER
                ) {
                    val diffChangedCenter = lastLayoutMeasureHeight / 2 - this.measuredHeight / 2
                    windowParams.y += diffChangedCenter
                }

                lastLayoutMeasureWidth = this.measuredWidth
                lastLayoutMeasureHeight = this.measuredHeight

                // 更新浮窗位置信息
                windowManager.updateViewLayout(decorView, windowParams)
            }
        }
    }

    private fun initEditText() {
        if (config.hasEditText) decorView?.let { traverseViewGroup(it) }
    }

    private fun traverseViewGroup(view: View?) {
        view?.let {
            // 遍历ViewGroup，是子view判断是否是EditText，是ViewGroup递归调用
            if (it is ViewGroup) for (i in 0 until it.childCount) {
                val child = it.getChildAt(i)
                if (child is ViewGroup) traverseViewGroup(child) else checkEditText(child)
            } else checkEditText(it)
        }
    }

    private fun checkEditText(view: View) {
        if (view is EditText) InputMethodUtils.initInputMethod(view, config.floatTag)
    }

    /**
     * 入场动画
     */
    private fun enterAnim(contentView: View) {
        Log.d(TAG, "enterAnim:")
        if (decorView == null || config.isAnim) return
        enterAnimator = AnimatorManager(decorView!!, windowParams, windowManager, config)
            .enterAnim()?.apply {
                // 可以延伸到屏幕外，动画结束按需去除该属性，不然旋转屏幕可能置于屏幕外部
                windowParams.flags =
                    (WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL
                            or WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
                            or WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS)

                addListener(object : Animator.AnimatorListener {
                    override fun onAnimationRepeat(animation: Animator?) {}

                    override fun onAnimationEnd(animation: Animator?) {
                        config.isAnim = false
                        if (!config.immersionStatusBar) {
                            // 不需要延伸到屏幕外了，防止屏幕旋转的时候，浮窗处于屏幕外
                            windowParams.flags = (WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL
                                    or WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE)
                        }
                        initEditText()
                    }

                    override fun onAnimationCancel(animation: Animator?) {}

                    override fun onAnimationStart(animation: Animator?) {
                        contentView.visibility = View.VISIBLE
                        config.isAnim = true
                    }
                })
                start()
            }
        if (enterAnimator == null) {
            contentView.visibility = View.VISIBLE
            windowManager.updateViewLayout(decorView, windowParams)
        }
    }

    /**
     * 退出动画
     */
    fun exitAnim() {
        if (decorView == null || (config.isAnim && enterAnimator == null)) return
        enterAnimator?.cancel()
        val animator: Animator? =
            AnimatorManager(decorView!!, windowParams, windowManager, config).exitAnim()
        if (animator == null) remove() else {
            // 二次判断，防止重复调用引发异常
            if (config.isAnim) return
            config.isAnim = true
            windowParams.flags = (WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL
                    or WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
                    or WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS)
            animator.addListener(object : Animator.AnimatorListener {
                override fun onAnimationRepeat(animation: Animator?) {}

                override fun onAnimationEnd(animation: Animator?): Unit {
                    remove()
                }

                override fun onAnimationCancel(animation: Animator?) {}

                override fun onAnimationStart(animation: Animator?) {}
            })
            animator.start()
        }
    }

    /**
     * 退出动画执行结束/没有退出动画，进行回调、移除等操作
     */
    fun remove(force: Boolean = false) = try {
        config.isAnim = false
        FloatingWindowManager.remove(config.floatTag)
        // removeView是异步删除，在Activity销毁的时候会导致窗口泄漏，所以使用removeViewImmediate直接删除view
        windowManager.run { if (force) removeViewImmediate(decorView) else removeView(decorView) }
    } catch (e: Exception) {
        Log.e(TAG, "浮窗关闭出现异常：$e")
    }


    interface CreateCallback {
        fun onCreate(success: Boolean)
    }

}