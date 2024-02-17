package com.wyj.view.base;

import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.animation.LinearInterpolator;

import com.wyj.view.R;

public class ClockView extends View {
    private static final String TAG = "ClockView";
    private static final int DEGREE_COUNT = 12;

    private int mBorderWidth;
    private int mMainLineLength; //主刻度线的长度
    private int mMainLineWidth; //主刻度线的宽度
    private int mSubLineLength; //副刻度线的长度
    private int mSubLineWidth; //副刻度线的宽度
    private int mPointerRadius; //指针圆弧部分的半径
    private RectF mPointerRectF; //指针圆弧部分所在的矩形边界
    private Paint mPaint;
    private int mClockColor; //绘制圆环以及刻度的颜色
    private int mPointerColor; //绘制指针的颜色
    private Path mPointerPath; //绘制指针的Path
    private ObjectAnimator mAnim;
    private int mCurAngle;


    public ClockView(Context context) {
        this(context, null);
    }

    public ClockView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ClockView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        mBorderWidth = dpToPx(5f);
        mMainLineLength = dpToPx(10) + mBorderWidth;
        mMainLineWidth = dpToPx(5);

        mSubLineLength = dpToPx(5) + mBorderWidth;
        mSubLineWidth = dpToPx(3);

        mPointerRadius = dpToPx(8);
        mPointerRectF = new RectF(-mPointerRadius,
                -mPointerRadius,
                mPointerRadius,
                mPointerRadius);

        mPaint = new Paint();
        mPaint.setAntiAlias(true);

        mClockColor = ContextCompat.getColor(getContext(), R.color.canvas_red_color);
        mPointerColor = ContextCompat.getColor(getContext(), R.color.canvas_orange_color);

        mPointerPath = new Path();

        mAnim = ObjectAnimator.ofInt(this, "angle", 0, 360);
        mAnim.setRepeatCount(ValueAnimator.INFINITE);
        mAnim.setInterpolator(new LinearInterpolator());
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        Log.d(TAG, "onSizeChanged: wyj");
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        int w = getWidth();
        int h = getHeight();

        int width = Math.min(w, h);
        canvas.translate(w / 2, h / 2); //将坐标系的圆点移动到控件的中心。
        width = width - mBorderWidth * 2;
        drawBorder(canvas, width);
        drawPointer(canvas, width);
    }

    private void drawBorder(Canvas canvas, int width) {
        canvas.save();
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeWidth(mBorderWidth);
        mPaint.setColor(mClockColor);
        canvas.drawCircle(0, 0, width / 2, mPaint);

        int angle = 360 / DEGREE_COUNT;
        for (int i = 0; i < DEGREE_COUNT; i++) {
            if (i % 3 == 0) {
                mPaint.setStrokeWidth(mMainLineWidth);
                canvas.drawLine(0, - width/2, 0, -width/2 + mMainLineLength, mPaint);
            } else {
                mPaint.setStrokeWidth(mSubLineWidth);
                canvas.drawLine(0, - width/2, 0, -width/2 + mSubLineLength, mPaint);
            }
            canvas.rotate(angle);
        }
        canvas.restore();
    }

    private void drawPointer(Canvas canvas, int width) {
        canvas.save();
        canvas.rotate(mCurAngle);
        if (mPointerPath.isEmpty()) {
            mPointerPath.moveTo(mPointerRadius, 0);
            mPointerPath.addArc(mPointerRectF, 0, 180);
            mPointerPath.lineTo(0, -width / 4 * 1.6f);
            mPointerPath.close();
        }
        mPaint.setStyle(Paint.Style.FILL);
        mPaint.setColor(mPointerColor);
        canvas.drawPath(mPointerPath, mPaint);
        canvas.restore();
    }

    public void start(long duration) {
        mAnim.setDuration(duration);
        mAnim.start();
    }

    public void setAngle(int angle) {
        mCurAngle = angle;
        postInvalidate();
    }

    /**
     * 转换 dp 至 px
     *
     * @param dpValue dp值
     * @return px值
     */
    protected int dpToPx(float dpValue) {
        DisplayMetrics metrics = Resources.getSystem().getDisplayMetrics();
        return (int) (dpValue * metrics.density + 0.5f);
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        if (mAnim != null) {
            mAnim.cancel();
        }
    }
}
