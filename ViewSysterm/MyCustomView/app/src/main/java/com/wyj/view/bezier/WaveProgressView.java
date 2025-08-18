package com.wyj.view.bezier;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PointF;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.animation.LinearInterpolator;

import com.wyj.view.R;

/**
 *  https://juejin.cn/post/6844903750750978055
 *  https://wslaimin.github.io/2019/07/10/%E6%B0%B4%E6%B3%A2%E7%BA%B9%E8%BF%9B%E5%BA%A6%E6%9D%A1/
 */

public class WaveProgressView extends View {
    private static final String TAG = "WaveView";
    //默认画笔颜色
    private final static int DEFAULT_COLOR = 0xFFFF0000;
    //从0%到100%进度默认动画时长
    private final static int DEFAULT_TOTAL_DURATION = 5000;
    //X方向上移默认移动速度
    private final static int DEFAULT_X_VELOCITY = 40;
    //X方向上移动速度
    private float mXVelocity;
    //波的周期
    private float mWavePeriod;
    //振幅
    private float mAmplitude;
    //0%到100%进度动画时长
    private int mTotalDuration;
    //绘制起始点坐标
    private PointF mStartPoint;
    //画笔
    private Paint mPaint;
    //正弦路径
    private Path mWavePath;
    //startPoint在X方向上移动的距离
    private float mTotalOffsetX = 0;
    //进度
    private int mProgress;
    //剪切画布路径
    private Path mClipPath;
    //画笔颜色
    private int mColor;
    //动画延迟时间
    private long mAnimationDelay = 0;
    //圆半径
    private float mRadius;
    private ValueAnimator mWaveMoveAnimator;

    public WaveProgressView(Context context) {
        this(context, null);
    }

    public WaveProgressView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.WaveView);
        mXVelocity = a.getFloat(R.styleable.WaveView_x_velocity, DEFAULT_X_VELOCITY);
        mTotalDuration = a.getInt(R.styleable.WaveView_total_duration, DEFAULT_TOTAL_DURATION);

        mColor = a.getColor(R.styleable.WaveView_color, DEFAULT_COLOR);
        a.recycle();
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaint.setColor(mColor);

        mStartPoint = new PointF();
        mWavePath = new Path();
        mClipPath = new Path();
    }

    public void reset(){
        mProgress=0;
        mAnimationDelay=0;
        mTotalOffsetX=0;
        mStartPoint.x=-mWavePeriod;
        int h = getMeasuredHeight();
        float gap = (h - 2 * mRadius) / 2f;
        mStartPoint.y = h - gap + mAmplitude;
        invalidate();
    }

    public void startAnim() {
        mWaveMoveAnimator = ValueAnimator.ofInt(0, 100);
        mWaveMoveAnimator.setDuration(1050);
        mWaveMoveAnimator.setInterpolator(new LinearInterpolator());
        mWaveMoveAnimator.setRepeatCount(ValueAnimator.INFINITE);
        mWaveMoveAnimator.start();

        mWaveMoveAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                int currentValue = (int) animation.getAnimatedValue();
//                mStartPoint.x = mWavePeriod * currentValue / 100  - mWavePeriod;
                mTotalOffsetX += mXVelocity / 6;
                mTotalOffsetX %= mWavePeriod;
                mStartPoint.x = -mWavePeriod + mTotalOffsetX;
                invalidate();
            }
        });
    }

    public int getProgress() {
        return mProgress;
    }

    public float getWaveLength() {
        return mWavePeriod;
    }

    public void setWaveLength(float waveLength) {
        this.mWavePeriod = waveLength;
        invalidate();
    }

    public float getPeak() {
        return mAmplitude;
    }

    public void setPeak(float peak) {
        this.mAmplitude = peak;
        invalidate();
    }

    public void setWaveColor(int color) {
        mColor = color;
        mPaint.setColor(mColor);
        invalidate();
    }

    public int getWaveColor() {
        return mColor;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        Log.d(TAG, "onMeasure: wyj width:" + getMeasuredWidth() + " height:" + getMeasuredHeight());
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mRadius = w < h ? w / 2f : h / 2f;
        mWavePeriod = 3 * mRadius / 2;
        mAmplitude = mWavePeriod / 11;
        //初始化StartPoint,左边预留出一个波周期的长度
        mStartPoint.x = -mWavePeriod;
        float gap = (h - 2 * mRadius) / 2f;
        mStartPoint.y = h - gap + mAmplitude;
        mClipPath.addCircle(w / 2f, h / 2f, mRadius, Path.Direction.CCW);
    }


    /**
     *  用属性动画来设置progress
     *  动画startValue为0，endValue为指定的进度。
     *  进度从0变化到100，动画的总时长为mTotalDuration，则可以计算设置指定进度
     *  的动画时长。
     *  设置动画插值器为线性的。
     *
     *  每次刷新时，mTotalOffsetX加上X速度
     *  mTotalOffsetX += mXVelocity;
     *  mTotalOffsetX超过一个周期时，mTotalOffsetX对波的周期取模，重置mTotalOffsetX
     */
    public void setProgress(final int progress) {
        if (mProgress == progress) {
            return;
        }
        final int preProgress = mProgress;
        //到达100%后为过滤波谷露出的额外进度
        if (progress <= 100) {
            mProgress = progress;
        }
        //动画时间=0%到100%的时间*（progress-mProgress)/100
        long duration = (long) mTotalDuration / 100 * (progress - preProgress);
        Log.d(TAG, "setProgress: wyj preProgress:" + preProgress + " progress:" + progress);
        ValueAnimator animator = ValueAnimator
                .ofInt(preProgress, progress)
                .setDuration(duration);
        animator.setInterpolator(new LinearInterpolator());
        animator.setStartDelay(mAnimationDelay);
        mAnimationDelay += duration;
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            int lastProgress;

            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                int nowProgress = (int) animation.getAnimatedValue();
                //出现相同progress不刷新，保证动画的平稳流畅
                if (lastProgress != nowProgress) {
                    lastProgress = nowProgress;
                    // 每次刷新时，mTotalOffsetX加上X速度
                    mTotalOffsetX += mXVelocity;
                    // mTotalOffsetX超过一个周期时，mTotalOffsetX对波的周期取模，重置mTotalOffsetX
                    mTotalOffsetX %= mWavePeriod;
                    mStartPoint.x = -mWavePeriod + mTotalOffsetX;
                    int h = getMeasuredHeight();
                    float gap = (h - 2 * mRadius) / 2f;
                    mStartPoint.y = h - gap + mAmplitude - lastProgress / 100f * (h + 2 * mAmplitude);
                    invalidate();
                }

                if (lastProgress == progress) {
                    startAnim();
                }
            }
        });

        animator.start();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.clipPath(mClipPath);
//        canvas.drawColor(Color.parseColor("#ffBEE0C0"));
        mWavePath.reset();
        mWavePath.moveTo(mStartPoint.x, mStartPoint.y);

        //波峰或波谷坐标
        float peakX, peakY;
        //每隔半个周期X坐标
        float nowX = mStartPoint.x;

        boolean isPeak = true;
        // 起始点在平衡线上，通过二阶贝塞尔曲线来模拟正弦波，先画一个波峰，再画一个波谷；起始点
        // 是二阶贝塞尔曲线的第一个数据点，这样由第一个数据点和波的周期可以计算二阶
        // 贝塞尔曲线其他的数据点和控制点；通过while循环移动数据点的坐标，并绘制二阶贝塞尔曲线的方式绘制正弦波，直到起始点的
        // x坐标到达视图的右边界为止。
        while (nowX <= getMeasuredWidth()) {
            peakX = nowX + mWavePeriod / 4;
            // 二阶贝塞尔曲线，当控制点的横坐标位于两个数据点横坐标的中间时，曲线上横坐标为两个数据点横坐标中间的点对应的
            // u为0.5，该点的纵坐标为控制点坐标的0.5，即控制点纵坐标是振幅的2倍。
            peakY = mStartPoint.y + (isPeak ? - 2 * mAmplitude : 2 * mAmplitude);
            isPeak = !isPeak;
            nowX += mWavePeriod / 2f;
            mWavePath.quadTo(peakX, peakY, nowX, mStartPoint.y);
        }

        mWavePath.lineTo(nowX, getMeasuredHeight());
        mWavePath.lineTo(mStartPoint.x, getMeasuredHeight());
        mWavePath.close();

        canvas.drawPath(mWavePath, mPaint);
        canvas.restore();
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        if (mWaveMoveAnimator != null) {
            mWaveMoveAnimator.cancel();
        }
    }
}
