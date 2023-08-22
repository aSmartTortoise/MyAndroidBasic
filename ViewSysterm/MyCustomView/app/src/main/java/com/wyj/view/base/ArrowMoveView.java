package com.wyj.view.base;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PathMeasure;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.LinearInterpolator;

import com.wyj.view.R;

public class ArrowMoveView extends View {
    private float currentValue = 0;     // 用于纪录当前的位置,取值范围[0,1]映射Path的整个长度

    private float[] pos;                // 当前点的实际位置
    private float[] tan;                // 当前点的tangent值,用于计算图片所需旋转的角度
    private Bitmap mBitmap;             // 箭头图片
    private Matrix mMatrix;             // 矩阵,用于对图片进行一些操作
    private int mWidth, mHeight;
    private Paint mPaint;
    private ValueAnimator valueAnimator;
    public ArrowMoveView(Context context) {
        this(context, null);
    }

    public ArrowMoveView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ArrowMoveView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        pos = new float[2];
        tan = new float[2];
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inSampleSize = 2;       // 缩放图片
        mBitmap = BitmapFactory.decodeResource(getContext().getResources(), R.drawable.arrow, options);
        mMatrix = new Matrix();

        mPaint = new Paint();
        mPaint.setColor(Color.BLACK);
        mPaint.setAntiAlias(true);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeWidth(3);
        mPaint.setTextSize(50);

        // 初始化 估值器 [区间0-1、时长5秒、线性增长、无限次循环]
        valueAnimator = ValueAnimator.ofFloat(0, 1f);
        valueAnimator.setDuration(5000);
// 匀速增长
        valueAnimator.setInterpolator(new LinearInterpolator());
//        valueAnimator.setRepeatCount(ValueAnimator.INFINITE);
        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                // 第一种做法：通过自己控制，是箭头在原来的位置继续运行
                currentValue += 0.005;
                if (currentValue >= 1) {
                    currentValue -= 1;
                }

                // 第二种做法：直接获取可以通过估值器，改变其变动规律
                //mCurrentValue = (float) animation.getAnimatedValue();

                invalidate();
            }
        });
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mWidth = w;
        mHeight = h;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.translate(mWidth / 2, mHeight / 2); // 平移坐标系

        Path path = new Path(); // 创建 Path

        path.addCircle(0, 0, 200, Path.Direction.CW);// 添加一个圆形

        PathMeasure measure = new PathMeasure(path, false); // 创建 PathMeasure


        mMatrix.reset(); // 重置Matrix
        measure.getPosTan(measure.getLength() * currentValue, pos, tan); // 获取当前位置的坐标以及趋势

        float degrees = (float) (Math.atan2(tan[1], tan[0]) * 180.0 / Math.PI); // 计算图片旋转角度

        // 旋转图片 角度为degrees
        mMatrix.postRotate(degrees, mBitmap.getWidth() / 2, mBitmap.getHeight() / 2);
        // 将图片绘制中心调整到与当前点重合
        mMatrix.postTranslate(pos[0] - mBitmap.getWidth() / 2, pos[1] - mBitmap.getHeight() / 2);

        canvas.drawPath(path, mPaint); // 绘制 Path
        canvas.drawBitmap(mBitmap, mMatrix, mPaint);  // 绘制箭头
    }

    public void startLoading() {
        valueAnimator.start();
    }

    public void stopLoading() {
        if (valueAnimator != null) {
            valueAnimator.cancel();
        }
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        startLoading();
    }
}
