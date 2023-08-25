package com.wyj.view.base;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;

import com.wyj.view.R;

/**
 * https://www.gcssloop.com/customview/Canvas_BasicGraphics.html
 */
public class CanvasApiView extends View {
    private Paint mPaint;

    public CanvasApiView(Context context) {
        this(context, null);
    }

    public CanvasApiView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CanvasApiView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        mPaint = new Paint();
        mPaint.setColor(Color.BLACK);       //设置画笔颜色
        mPaint.setStyle(Paint.Style.FILL);  //设置画笔模式为填充
        mPaint.setStrokeWidth(10f);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
//        drawColor(canvas);
//        drawPoint(canvas);
        drawArc(canvas);

    }

    /**
     * 填充整个画布，用来绘制底色。
     */
    private void drawColor(Canvas canvas) {
        canvas.drawColor(Color.CYAN);
    }

    /**
     * 坐标系原点默认为View的左上角。
     */
    private void drawPoint(Canvas canvas) {
        canvas.drawPoint(200, 200, mPaint);
        canvas.drawPoints(new float[]{
                        500, 500, 500, 600, 500, 700
                }
                , mPaint);
    }

    private void drawArc(Canvas canvas) {
        RectF rectF = new RectF(100, 100, 800, 400);
        // 绘制背景矩形
        mPaint.setColor(Color.GRAY);
        canvas.drawRect(rectF, mPaint);

        // 绘制圆弧
        mPaint.setColor(Color.BLUE);
        canvas.drawArc(rectF, 0, 90, true, mPaint);
    }
}
