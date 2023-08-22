package com.wyj.view.base;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PathMeasure;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

/**
 *  https://www.gcssloop.com/customview/Path_PathMeasure.html
 */
public class PathMeasureApiView extends View {
    private static final String TAG = "PathMeasureApiView";
    private Paint mPaint;
    private int mWidth, mHeight;
    public PathMeasureApiView(Context context) {
        this(context, null);
    }

    public PathMeasureApiView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public PathMeasureApiView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
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
        mPaint.setStrokeWidth(6);
        mPaint.setTextSize(50);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.translate(mWidth/2, mHeight/2);
        Path path = new Path();

//        pathMeasureConstructor(canvas, path);
//        getSegment(canvas, path);
        nextContour(canvas, path);
    }

    /**
     *  如果Path是开放的图形，使用PathMeasure测量Path的长度时，如果参数forceClosed为true，
     *  测量的结果比Path实际长度要长一些，实际获得的时Path闭合状态下的长度。
     *  通过PathMeasure关联Path并不会影响Path的状态。
     */
    private void pathMeasureConstructor(Canvas canvas, Path path) {
        path.lineTo(0, 200);
        path.lineTo(200, 200);
        path.lineTo(200, 0);

        PathMeasure measure1 = new PathMeasure(path, false);
        PathMeasure measure2 = new PathMeasure(path, true);

        Log.d(TAG, "forceClosed=false---->"+measure1.getLength());
        Log.d(TAG, "forceClosed=true----->"+measure2.getLength());

        canvas.drawPath(path,mPaint);
    }

    /**
     *  用于获取Path的一个片段，
     *  (1 )如果 startD、stopD 的数值不在取值范围 [0, getLength] 内，或者 startD == stopD 则返回值为 false，
     *  不会改变 dst 内容。
     *  (2) 截取的片段会添加到dst的Path中。
     *  (3) 如果 startWithMoveTo 为 true, 则被截取出来到Path片段保持原状，如果 startWithMoveTo 为 false，
     *  则会将截取出来的 Path 片段的起始点移动到 dst 的最后一个点，以保证 dst 的连续性。
     */
    private void getSegment(Canvas canvas, Path path) {
        path.addRect(-200, -200, 200, 200, Path.Direction.CW);
        Path dst = new Path(); // 创建用于存储截取后内容的 Path
        dst.lineTo(-300, -300);

        PathMeasure measure = new PathMeasure(path, false); // 将 Path 与 PathMeasure 关联

        // 截取一部分存入dst中，并使用 moveTo 保持截取得到的 Path 第一个点的位置不变
        measure.getSegment(200, 600, dst, true);

        canvas.drawPath(dst, mPaint);
    }

    private void nextContour(Canvas canvas, Path path) {
        path.addRect(-100, -100, 100, 100, Path.Direction.CW);  // 添加小矩形
        path.addRect(-200, -200, 200, 200, Path.Direction.CW);  // 添加大矩形
        canvas.drawPath(path, mPaint); // 绘制 Path
        PathMeasure measure = new PathMeasure(path, false);// 将Path与PathMeasure关联
        float len1 = measure.getLength(); // 获得第一条路径的长度
        measure.nextContour(); // 跳转到下一条路径
        float len2 = measure.getLength(); // 获得第二条路径的长度
        Log.d(TAG,"nextContour len1="+len1); // 输出两条路径的长度
        Log.i(TAG, "nextContour len2="+len2);
    }
}
