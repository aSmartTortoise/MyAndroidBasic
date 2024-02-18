package com.wyj.view.base;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import androidx.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;

public class PathOpView extends View {
    private Paint mPaint;
    private int mWidth, mHeight;
    public PathOpView(Context context) {
        this(context, null);
    }

    public PathOpView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public PathOpView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
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
        mPaint.setStyle(Paint.Style.FILL);
        mPaint.setStrokeWidth(10);
        mPaint.setTextSize(50);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
//        yinYangFish(canvas);
        op(canvas);
    }

    private void yinYangFish(Canvas canvas) {
        canvas.translate(mWidth/2, mHeight/2);
        Path path1 = new Path();
        Path path2 = new Path();
        Path path3 = new Path();
        Path path4 = new Path();

        path1.addCircle(0, 0, 200, Path.Direction.CW);
        path2.addRect(0, -200, 200, 200, Path.Direction.CW);
        path3.addCircle(0, -100, 100, Path.Direction.CW);
        path4.addCircle(0, 100, 100, Path.Direction.CCW);

        path1.op(path2, Path.Op.DIFFERENCE);
        path1.op(path3, Path.Op.UNION);
        path1.op(path4, Path.Op.DIFFERENCE);

        canvas.drawPath(path1, mPaint);
//        canvas.drawPath(path2, mPaint);
//        canvas.drawPath(path3, mPaint);
//        canvas.drawPath(path4, mPaint);
    }

    private void op(Canvas canvas) {
        int x = 80;
        int r = 100;
        canvas.restore();
        canvas.translate(250,0);

        Path path1 = new Path();
        Path path2 = new Path();
        Path pathOpResult = new Path();

        path1.addCircle(-x, 0, r, Path.Direction.CW);
        path2.addCircle(x, 0, r, Path.Direction.CW);

        pathOpResult.op(path1,path2, Path.Op.DIFFERENCE);
        canvas.translate(0, 200);
        canvas.drawText("DIFFERENCE", 240,0,mPaint);
        canvas.drawPath(pathOpResult,mPaint);

        pathOpResult.op(path1,path2, Path.Op.REVERSE_DIFFERENCE);
        canvas.translate(0, 300);
        canvas.drawText("REVERSE_DIFFERENCE", 240,0,mPaint);
        canvas.drawPath(pathOpResult,mPaint);
//
        pathOpResult.op(path1,path2, Path.Op.INTERSECT);
        canvas.translate(0, 300);
        canvas.drawText("INTERSECT", 240,0,mPaint);
        canvas.drawPath(pathOpResult,mPaint);

        pathOpResult.op(path1,path2, Path.Op.UNION);
        canvas.translate(0, 300);
        canvas.drawText("UNION", 240,0,mPaint);
        canvas.drawPath(pathOpResult,mPaint);

        pathOpResult.op(path1,path2, Path.Op.XOR);
        canvas.translate(0, 300);
        canvas.drawText("XOR", 240,0,mPaint);
        canvas.drawPath(pathOpResult,mPaint);
    }
}
