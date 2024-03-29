package com.wyj.view.bezier;

import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PointF;
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
    //波长
    private float mWaveLength;
    //波峰
    private float mPeak;
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
    private int mAnimationDelay = 0;
    //startPoint在Y方向初始坐标(控件高度大于宽度，进度在Y方向上位移范围为2*radius，非控件高度)
    private float mProgressBottom;
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

    /**
     * 用属性动画来设置progress，因为过快的刷新速度会看不到进度效果
     *
     * @param progress 设置的进度
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
        long duration = mTotalDuration / 100 * (progress - preProgress);
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
                    mTotalOffsetX += mXVelocity;
                    mTotalOffsetX %= mWaveLength;
                    mStartPoint.x = -mWaveLength + mTotalOffsetX;
                    mStartPoint.y = mProgressBottom - 2 * mRadius * nowProgress / 100f;
                    invalidate();
                }
            }
        });
        animator.start();


        //进度到100%时防止波谷露出
        if (progress == 100) {
            //0%防止波峰露出，位置下移一个振幅，100%防止波谷露出，位置上一一个振幅，所以额外移动距离为2*mPeak
            //extraProgress = (int) (2*mPeak / (2*mRadius) * 100)
            int extraProgress = (int) (mPeak / mRadius * 100);
            setProgress(extraProgress == 0 ? 101 : 100 + extraProgress);
        }
    }

    public void reset(){
        mProgress=0;
        mAnimationDelay=0;
        mTotalOffsetX=0;
        mStartPoint.x=-mWaveLength;
        mStartPoint.y=mProgressBottom;
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
                mStartPoint.x = mWaveLength * currentValue / 100  - mWaveLength;
                postInvalidate();
            }
        });
    }



    public int getProgress() {
        return mProgress;
    }

    public float getWaveLength() {
        return mWaveLength;
    }

    public void setWaveLength(float waveLength) {
        this.mWaveLength = waveLength;
        invalidate();
    }

    public float getPeak() {
        return mPeak;
    }

    public void setPeak(float peak) {
        this.mPeak = peak;
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
        mClipPath.addCircle(w / 2f, h / 2f, mRadius, Path.Direction.CCW);
        mWaveLength = 3 * mRadius / 2;
        mPeak = mWaveLength / 11;
        //初始化StartPoint,左边预留出一个周期的移动长度
        mStartPoint.x = -mWaveLength;
        setMyProgress(0);
    }

    public void setMyProgress(float progress) {
        int h = getMeasuredHeight();
        float gap = (h - 2 * mRadius) / 2f;
        mProgressBottom = h - gap + mPeak - progress * (h + 2 * mPeak);
        mStartPoint.y = mProgressBottom;
        postInvalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.clipPath(mClipPath);
        canvas.drawColor(Color.parseColor("#ffBEE0C0"));
        mWavePath.reset();
        mWavePath.moveTo(mStartPoint.x, mStartPoint.y);

        //波峰或波谷坐标
        float peakX, peakY;
        //每隔半个周期X坐标
        float nowX = mStartPoint.x;

        boolean isPeak = true;
        while (nowX <= getMeasuredWidth()) {
            peakX = nowX + mWaveLength / 4f;
            // 二阶贝塞尔曲线，当控制点的横坐标位于两个数据点横坐标的中间时，曲线上横坐标为两个数据点横坐标中间的点对应的
            // u为0.5，该点的纵坐标为控制点坐标的0.5，即控制点纵坐标是振幅的2倍。
            peakY = mStartPoint.y + (isPeak ? - 2 * mPeak : 2 * mPeak);
            isPeak = !isPeak;
            nowX += mWaveLength / 2f;
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
