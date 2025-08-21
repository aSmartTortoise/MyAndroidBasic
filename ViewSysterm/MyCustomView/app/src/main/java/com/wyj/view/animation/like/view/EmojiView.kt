package com.test.magictextview.like.view

import android.animation.Animator
import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.annotation.SuppressLint
import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.util.Log
import android.view.View
import android.view.animation.AccelerateInterpolator
import android.view.animation.DecelerateInterpolator
import android.view.animation.LinearInterpolator
import com.wyj.view.animation.like.factory.BitmapProvider

/**
 * 表情动画view
 * @author xiaoman
 */
@SuppressLint("ViewConstructor")
class EmojiView @JvmOverloads constructor(
    context: Context,
    private val provider: BitmapProvider.Provider?,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    companion object {
        //动画时长
        const val DURATION = 500
        const val TAG = "EmojiAnimationView"
    }

    private var mThumbImage: Bitmap? = null
    private var mBitmapPaint: Paint? = null
    private var mAnimatorListener: AnimatorListener? = null

    /**
     * 表情图标的宽度
     */
    private var emojiWidth = 0

    /**
     * 表情图标的高度
     */
    private var emojiHeight = 0


    init {
        //初始化paint
        mBitmapPaint = Paint(Paint.ANTI_ALIAS_FLAG)
    }

    fun setEmoji() {
        //随机取出表情bitmap，对成员变量mThumbImage赋值。触发绘制机制。
        mThumbImage = provider?.randomBitmap
        mThumbImage?.let {
            emojiWidth = it.width
            emojiHeight = it.height
            invalidate()
        }
    }

    @SuppressLint("DrawAllocation")
    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        mThumbImage?.let {
            val dst = Rect()
            dst.left = 0
            dst.top = 0
            dst.right = emojiWidth
            dst.bottom = emojiHeight
            canvas.drawBitmap(it, null, dst, mBitmapPaint)
        }
    }

    /**
     * 开始动画
     */
    fun startAnimation() {
        val topX = -1080 + (1400 * Math.random()).toFloat()
        val topY = -300 + (-700 * Math.random()).toFloat()
        Log.d(TAG, "startAnimation: wyj topX:$topX, topY:$topY")
        //上升动画
        val translateAnimationX = ObjectAnimator.ofFloat(this, "translationX", 0f, topX)
        translateAnimationX.duration = DURATION.toLong()
        translateAnimationX.interpolator = LinearInterpolator()
        val translateAnimationY = ObjectAnimator.ofFloat(this, "translationY", 0f, topY)
        translateAnimationY.duration = DURATION.toLong()
        translateAnimationY.interpolator = DecelerateInterpolator()
        //透明度变化
        val alphaAnimation = ObjectAnimator.ofFloat(
            this,
            "alpha",
            1.0f,
            1.0f,
            1.0f,
            0.9f,
            0.8f,
            0.8f,
            0.7f,
            0.6f,
        )
        alphaAnimation.duration = DURATION.toLong()

        val animatorSetUp = AnimatorSet()
        animatorSetUp
            .play(translateAnimationX)
            .with(translateAnimationY)
            .with(alphaAnimation)

        //下降动画
        val durationDown = (DURATION / 5).toLong()
        val translateAnimationXDown =
            ObjectAnimator.ofFloat(this, "translationX", topX, topX * 1.2f)
        translateAnimationXDown.duration = durationDown
        translateAnimationXDown.interpolator = LinearInterpolator()
        val translateAnimationYDown =
            ObjectAnimator.ofFloat(this, "translationY", topY, topY * 0.8f)
        translateAnimationYDown.duration = durationDown
        translateAnimationYDown.interpolator = AccelerateInterpolator()
        val alphaAnimationDown = ObjectAnimator.ofFloat(this, "alpha", 0.6f, 0.0f)
        alphaAnimationDown.duration = durationDown

        val animatorSetDown = AnimatorSet()

        animatorSetUp.start()
        animatorSetUp.addListener(object : Animator.AnimatorListener {
            override fun onAnimationStart(animation: Animator) {}
            override fun onAnimationEnd(animation: Animator) {
                animatorSetDown
                    .play(translateAnimationXDown)
                    .with(translateAnimationYDown)
                    .with(alphaAnimationDown)
                animatorSetDown.start()
            }

            override fun onAnimationCancel(animation: Animator) {}
            override fun onAnimationRepeat(animation: Animator) {}
        })

        animatorSetDown.addListener(object : Animator.AnimatorListener {
            override fun onAnimationStart(animation: Animator) {}
            override fun onAnimationEnd(animation: Animator) {
                //动画完成后通知移除动画view
                mAnimatorListener?.onEmojiAnimationEnd()
            }

            override fun onAnimationCancel(animation: Animator) {}
            override fun onAnimationRepeat(animation: Animator) {}
        })
    }


    /**
     * 这些get\set方法用于表情图标的大小动画
     * 不能删除
     */
    fun getEmojiWith(): Int {
        return emojiWidth
    }

    fun setEmojiWith(emojiWith: Int) {
        this.emojiWidth = emojiWith
    }

    fun getEmojiHeight(): Int {
        return emojiHeight
    }

    fun setEmojiHeight(emojiHeight: Int) {
        this.emojiHeight = emojiHeight
    }

    fun setAnimatorListener(animatorListener: AnimatorListener?) {
        mAnimatorListener = animatorListener
    }

    interface AnimatorListener {
        fun onEmojiAnimationEnd()
    }




}
