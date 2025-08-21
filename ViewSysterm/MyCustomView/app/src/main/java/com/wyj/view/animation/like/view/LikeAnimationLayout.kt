package com.wyj.view.animation.like.view

import android.content.Context
import android.util.AttributeSet
import android.util.Log
import android.view.ViewGroup
import android.widget.FrameLayout
import com.test.magictextview.like.view.EmojiView
import com.wyj.view.R
import com.wyj.view.animation.like.factory.BitmapProvider
import com.wyj.view.utils.PublicMethod

/**
 * 点赞动画布局view
 * @author xiaoman
 */
class LikeAnimationLayout @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr) {

    companion object {
        private const val TAG = "LikeAnimationLayout"

        /**
         * 表情动画单次弹出个数，以后如果有不同需求可以改为配置
         */
        private const val ERUPTION_ELEMENT_AMOUNT = 8
        private const val MAX_ANGLE = 180
        private const val MIN_ANGLE = 70
        private const val SPACING_TIME = 400L
        // 点赞次数与文案View和表情View垂直方向上的间距，dp。
        private const val BOTTOM_TOP_MARGIN = 40f
    }

    init {
        init(context, attrs, defStyleAttr)
    }

    private var lastClickTime: Long = 0
    private var currentNumber = 1
    private var mNumberLevelView: NumberLevelView? = null

    /**
     * 有无表情动画 暂时无用
     */
    private var hasEruptionAnimation = false

    /**
     * 有无等级文字 暂时无用
     */
    private var hasTextAnimation = false

    /**
     * 是否可以长按，暂时无用 目前用时间来管理
     */
    private var canLongPress = false

    /**
     * 最大和最小角度暂时无用
     */
    private var maxAngle = 0
    private var minAngle = 0

    private var pointX = 0
    private var pointY = 0
    var provider: BitmapProvider.Provider? = null
        get() {
            if (field == null) {
                field = BitmapProvider.Builder(context).build()
            }
            return field
        }


    private fun init(context: Context, attrs: AttributeSet?, defStyleAttr: Int) {
        val typedArray = context.obtainStyledAttributes(
            attrs,
                R.styleable.LikeAnimationLayout,
            defStyleAttr,
            0
        )
        maxAngle =
            typedArray.getInteger(R.styleable.LikeAnimationLayout_max_angle, MAX_ANGLE)
        minAngle =
            typedArray.getInteger(R.styleable.LikeAnimationLayout_min_angle, MIN_ANGLE)
        hasEruptionAnimation = typedArray.getBoolean(
                R.styleable.LikeAnimationLayout_show_emoji,
            true
        )
        hasTextAnimation = typedArray.getBoolean(R.styleable.LikeAnimationLayout_show_text, true)
        typedArray.recycle()

    }


    /**
     * 添加表情View和点赞次数和文案的View
     * @param x emojiView的marginLeft
     * @param y emojiView的marginTop
     */
    fun launch(x: Int, y: Int) {
        Log.d(TAG, "launch: wyj x:$x, y:$y")
        if (System.currentTimeMillis() - lastClickTime >= SPACING_TIME) {
            pointX = x
            pointY = y
            //单次点击
            addEmojiView(context, x, y)
            lastClickTime = System.currentTimeMillis()
            currentNumber = 1
            if (mNumberLevelView != null) {
                removeView(mNumberLevelView)
                mNumberLevelView = null
            }
        } else { //连续点击
            if (pointX != x || pointY != y){
                return
            }
            lastClickTime = System.currentTimeMillis()
            Log.i(TAG, "当前动画化正在执行")
            addEmojiView(context, x, y)

            if (mNumberLevelView == null) {
                //添加数字连击view
                val layoutParams = LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
                )
                layoutParams.setMargins(
                    0,
                    y - PublicMethod.dp2px(context, BOTTOM_TOP_MARGIN),
                    0,
                    0)
                mNumberLevelView = NumberLevelView(context, provider, x)
                addView(mNumberLevelView, layoutParams)
            }
            currentNumber++
            mNumberLevelView?.setNumber(currentNumber)
        }
    }

    /**
     * 添加表情动画view，并开始动画
     */
    private fun addEmojiView(context: Context?, x: Int, y: Int) {
        for (i in 1 .. ERUPTION_ELEMENT_AMOUNT) {
            val layoutParams = LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT)
            layoutParams.setMargins(x, y, 0, 0)
            val articleThumb = context?.let {
                EmojiView(it, provider)
            }

            articleThumb?.let {
                it.setEmoji()
                this.addView(it, -1, layoutParams)
                it.setAnimatorListener(object : EmojiView.AnimatorListener {
                    override fun onEmojiAnimationEnd() {
                        removeView(it)
                        postDelayed({
                            if (mNumberLevelView != null && System.currentTimeMillis() - lastClickTime >= SPACING_TIME) {
                                removeView(mNumberLevelView)
                                mNumberLevelView = null
                            }
                        }, SPACING_TIME)
                    }

                })
                it.startAnimation()
            }
        }
    }


}