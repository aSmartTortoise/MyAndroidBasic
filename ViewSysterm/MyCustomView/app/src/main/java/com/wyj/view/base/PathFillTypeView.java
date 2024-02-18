package com.wyj.view.base;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import androidx.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;

public class PathFillTypeView extends View {
    private Paint mPaint;
    private int mWidth, mHeight;

    public PathFillTypeView(Context context) {
        this(context, null);
    }

    public PathFillTypeView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public PathFillTypeView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mWidth = w;
        mHeight = h;
    }

    private void init() {
        mPaint = new Paint();
        mPaint.setColor(Color.BLACK);
        mPaint.setAntiAlias(true);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeWidth(10);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.translate(mWidth / 2, mHeight / 2);//移动坐标系到屏幕中心
        mPaint.setStyle(Paint.Style.FILL);


//        evenOddRule(canvas);
        nonZeroWindingNumberRule(canvas);

    }

    private void evenOddRule(Canvas canvas) {
        Path path = new Path();
        //        path.setFillType(Path.FillType.EVEN_ODD);//奇偶模式
        path.setFillType(Path.FillType.INVERSE_EVEN_ODD);//反奇偶模式
        path.addRect(-200, -200, 200, 200, Path.Direction.CW);
        canvas.drawPath(path, mPaint);
    }

    private void nonZeroWindingNumberRule(Canvas canvas) {
        Path path = new Path();
        // 添加小正方形 (通过这两行代码来控制小正方形边的方向,从而演示不同的效果)
        path.addRect(-200, -200, 200, 200, Path.Direction.CW);
//        path.addRect(-200, -200, 200, 200, Path.Direction.CCW);

        // 添加大正方形
        path.addRect(-400, -400, 400, 400, Path.Direction.CCW);

        path.setFillType(Path.FillType.WINDING);// 设置Path填充模式为非零环绕规则
        canvas.drawPath(path, mPaint);
    }

}
