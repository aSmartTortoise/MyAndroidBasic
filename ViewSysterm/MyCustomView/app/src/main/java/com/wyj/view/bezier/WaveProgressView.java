package com.wyj.view.bezier;

import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PointF;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.animation.LinearInterpolator;

import com.wyj.view.R;

/**
 *  https://juejin.cn/post/6844903750750978055
 */

public class WaveProgressView extends View {
    private static final String TAG = "WaveView";
    //默认波长
    private final static int DEFAULT_WAVE_LENGTH = 600;
    //默认波峰
    private final static int DEFAULT_PEAK = 60;
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
    //startPoint X方向开始偏移量
    private float mStartOffsetX;
    //波峰
    private float mPeak;
    //0%到100%进度动画时长
    private int mTotalDuration;
    //绘制起始点坐标
    private PointF mStartPoint;
    //画笔
    private Paint mPaint;
    //正弦路径
    private Path mPath;
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

    public WaveProgressView(Context context) {
        super(context);
    }

    public WaveProgressView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);

        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.WaveView);
        mWaveLength = a.getFloat(R.styleable.WaveView_wave_length, DEFAULT_WAVE_LENGTH);
        mPeak = a.getFloat(R.styleable.WaveView_peak, DEFAULT_PEAK);
        mStartOffsetX = a.getFloat(R.styleable.WaveView_start_offsetX, 0);
        mXVelocity = a.getFloat(R.styleable.WaveView_x_velocity, DEFAULT_X_VELOCITY);
        mTotalDuration = a.getInt(R.styleable.WaveView_total_duration, DEFAULT_TOTAL_DURATION);

        if (mStartOffsetX > 0) {
            throw new RuntimeException("startOffsetX不能为正数");
        }

        mColor = a.getColor(R.styleable.WaveView_color, DEFAULT_COLOR);
        a.recycle();
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaint.setColor(mColor);

        mStartPoint = new PointF();
        mPath = new Path();
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
                    mStartPoint.x = -mWaveLength + mStartOffsetX + mTotalOffsetX;
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
        mStartPoint.x=-mWaveLength + mStartOffsetX;
        mStartPoint.y=mProgressBottom;
        invalidate();
    }

    public void startAnim() {
        ValueAnimator animator = ValueAnimator.ofInt(0, 100);
        animator.setDuration(1500);
        animator.setInterpolator(new LinearInterpolator());
        animator.setRepeatCount(ValueAnimator.INFINITE);
        animator.start();

        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                int currentValue = (int) animation.getAnimatedValue();
                mStartPoint.x = mWaveLength * currentValue / 100 + (mStartOffsetX - mWaveLength);
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

    public float getStartOffsetX() {
        return mStartOffsetX;
    }

    public void setStartOffsetX(float startOffsetX) {
        this.mStartOffsetX = startOffsetX;
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
        getWaveHeight();

        //初始化StartPoint,左边预留出一个周期的移动长度
        mStartPoint.x = -mWaveLength;
        setMyProgress(0);
    }

    private void getWaveHeight() {
        PointF startPoint = new PointF();
        startPoint.x = 0;
        startPoint.y = 0;
        PointF controlPoint = new PointF();
        controlPoint.x = 1/4 * mWaveLength;
        PointF endPoint = new PointF();
        endPoint.x = 1/2 * mWaveLength;
        endPoint.y = 0;

    }

    public void setMyProgress(float progress) {
        int h = getMeasuredHeight();
        float gap = (h - 2 * mRadius) / 2f;

        mProgressBottom = h - gap + mPeak - progress * h;

        if (progress >= 1f) {
            //防止波谷露出
            mProgressBottom = h - gap + mPeak - progress * h - 2 * mPeak;
        }

        mStartPoint.y = mProgressBottom;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        canvas.clipPath(mClipPath);
        mPath.reset();
        mPath.moveTo(mStartPoint.x, mStartPoint.y);

        //波峰或波谷坐标
        float peakX, peakY;
        //每隔半个周期X坐标
        float nowX = mStartPoint.x;

        boolean isPeak = true;
        while (nowX < getMeasuredWidth()) {
            peakX = nowX + mWaveLength / 4f;
            // 二阶贝塞尔曲线，当控制点的横坐标位于两个数据点横坐标的中间时，曲线上横坐标为两个数据点横坐标中间的点对应的
            // u为0.5，该点的纵坐标为控制点坐标的0.5，即控制点纵坐标时振幅的2倍。
            peakY = mStartPoint.y + (isPeak ? - 2 * mPeak : 2 * mPeak);
            isPeak = !isPeak;
            nowX += mWaveLength / 2f;
            mPath.quadTo(peakX, peakY, nowX, mStartPoint.y);
        }

        mPath.lineTo(nowX, getMeasuredHeight());
        mPath.lineTo(mStartPoint.x, getMeasuredHeight());
        mPath.close();
        canvas.drawPath(mPath, mPaint);
    }
}
