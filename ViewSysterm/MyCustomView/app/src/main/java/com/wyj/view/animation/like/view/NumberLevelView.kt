package com.wyj.view.animation.like.view

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.View
import com.wyj.view.animation.like.factory.BitmapProvider
import com.wyj.view.utils.PublicMethod

/**
 * 数字和等级文案view
 * @author xiaoman
 */
@SuppressLint("ViewConstructor")
class NumberLevelView @JvmOverloads constructor(
    context: Context,
    private val provider: BitmapProvider.Provider?,
    private val x: Int,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {
    private var textPaint: Paint = Paint(Paint.ANTI_ALIAS_FLAG)

    /**
     * 点击次数
     */
    private var number = 0

    /**
     * 等级
     */
    private var level = 0


    /**
     * 数字图片的总宽度
     */
    private var offsetX = 0

    /**
     * x 初始位置
     */
    private var initialValue = 0

    /**
     * 默认数字和等级文案图片间距
     */
    private var spacing = 0

    init {
        spacing = PublicMethod.dp2px(context, 10f)
    }

    /**
     *  点赞次数和点赞文案的关系
     *  20次以内是 次赞；20-80次是 太棒了；超过80次是 超赞同。
     */
    fun setNumber(number: Int) {
        this.number = number
        if (this.number > 999){
            this.number = 999
        }
        level = when (this.number) {
            in 1..20 -> {
                0
            }
            in 21..80 -> {
                1
            }
            else -> {
                2
            }
        }
        invalidate()
    }

    @SuppressLint("DrawAllocation")
    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        val levelBitmap = provider?.getLevelBitmap(level) ?: return
        val levelBitmapWidth = levelBitmap.width
        val dstLevel = Rect()
        initialValue = x - levelBitmapWidth
        dstLevel.left =  initialValue
        dstLevel.right = initialValue + levelBitmapWidth
        dstLevel.top = 0
        dstLevel.bottom = levelBitmap.height
        //绘制等级文案图标
        canvas.drawBitmap(levelBitmap, null, dstLevel, textPaint)

        offsetX = 0
        var numberDraw = number
        while (numberDraw > 0) {
            val numberBit = numberDraw % 10
            val bitmap = provider.getNumberBitmap(numberBit) ?: continue
            offsetX += bitmap.width
            val dstNumber = Rect()
            dstNumber.top = 0
            dstNumber.bottom = bitmap.height
            dstNumber.left = initialValue - spacing - offsetX
            dstNumber.right = initialValue - spacing - offsetX + bitmap.width
            //绘制数字，依次绘制个位、十位、百位。
            canvas.drawBitmap(bitmap, null, dstNumber, textPaint)
            numberDraw /= 10
        }
    }

}