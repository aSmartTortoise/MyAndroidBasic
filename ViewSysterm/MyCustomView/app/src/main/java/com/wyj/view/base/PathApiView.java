package com.wyj.view.base;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

/**
 *  参考文章
 *  https://www.gcssloop.com/customview/Path_Basic.html
 *
 */
public class PathApiView extends View {
    private static final String TAG = "PathApiView";

    private Paint mPaint;
    private int mWidth, mHeight;
    public PathApiView(Context context) {
        this(context, null);
    }

    public PathApiView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public PathApiView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        Log.d(TAG, "onSizeChanged: ");
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
        Log.d(TAG, "onDraw: ");
        canvas.translate(mWidth/2, mHeight/2);//移动坐标系到屏幕中心
        Path path = new Path();

//        moveTo(canvas, path);
//        setLastPoint(canvas, path);
//        close(canvas, path);
//
//        addRect(canvas, path);
//        direction(canvas, path);
//
//        addPath(canvas, path);
//
        addArc(canvas, path);
//        arcTo(canvas, path);
//        arcToForceMoveTo(canvas, path);
//
//        isEmpty(canvas, path);
//        isRec(canvas, path);
//        setPath(canvas, path);
//        offset(canvas, path);
//
//        rLineTo(canvas, path);
//        computeBounds(canvas, path);
    }


    private void moveTo(Canvas canvas, Path path) {
        path.lineTo(200, 200);

        path.moveTo(200, 100);//移动下一次操作的起点

        path.lineTo(200, 0);
        canvas.drawPath(path,mPaint);
    }

    private void setLastPoint(Canvas canvas, Path path) {
        path.lineTo(200, 200);

        path.setLastPoint(200, 100);//设置之前操作的最后一个点的位置。

        path.lineTo(200, 0);
        canvas.drawPath(path,mPaint);
    }

    private void close(Canvas canvas, Path path) {
        path.lineTo(200, 200);
        path.lineTo(200, 0);
        path.close();//直线连接当前路径的最后一个点和第一个点（如果两个点不重合的化），形成一个闭合路径。
        canvas.drawPath(path,mPaint);
    }

    private void addRect(Canvas canvas, Path path) {
        path.addRect(-200, -200, 200, 200, Path.Direction.CCW);
        canvas.drawPath(path,mPaint);
    }

    /**
     *
     * Direction 指定将封闭形状添加到路径的方向。
     *  作用有：
     *      在添加形状时，指定闭合顺序（记录构成形状的点的顺序 顺时针或者逆时针）
     */
    private void direction(Canvas canvas, Path path) {
//        path.addRect(-200, -200, 200, 200, Path.Direction.CW);
        path.addRect(-200, -200, 200, 200, Path.Direction.CCW);
        path.setLastPoint(-300, 300);
        canvas.drawPath(path,mPaint);
    }

    /**
     *  先生成一个矩形的Path和一个圆形的Path，中心都在圆点，然后将圆形Path垂直向上移动了200像素后，
     *  将圆形Path添加到矩形Path中。
     *
     */
    private void addPath(Canvas canvas, Path path) {
        canvas.scale(1, -1); // 上下翻转Y轴
        path.addRect(-200, -200, 200, 200, Path.Direction.CW);
        Path src = new Path();
        src.addCircle(0, 0, 100, Path.Direction.CW);
        path.addPath(src, 0, 200);
        canvas.drawPath(path,mPaint);
    }

    /**
     *
     * 将一段圆弧添加到Path中。
     *
     */
    private void addArc(Canvas canvas, Path path) {
//        canvas.scale(1, -1);
        path.lineTo(100, 100);
        RectF oval = new RectF(0, 0, 300, 300);// 圆弧的外切矩形，该矩形确定了圆弧的半径
        path.addArc(oval, 45, 180);// startAngle- 开始的角度；sweepAngel- 扫过的角度[-360, 360)
        canvas.drawPath(path, mPaint);
    }

    /**
     *  将一段圆弧添加到Path中，如果圆弧的起点和Path最后操作的的点不重合，则连接圆弧的起点和Path的这个点。
     */
    private void arcTo(Canvas canvas, Path path) {
        canvas.scale(1, -1);
        path.lineTo(100, 100);
        RectF oval = new RectF(0, 0, 300, 300);
        path.arcTo(oval, 0, 345);// startAngle- 开始的角度；sweepAngel- 扫过的角度[-360, 360)
        canvas.drawPath(path,mPaint);
    }

    /**
     *  将一段圆弧添加到Path中，不连接圆弧的起点和Path最后操作的一个点。
     */
    private void arcToForceMoveTo(Canvas canvas, Path path) {
        canvas.scale(1, -1);
        path.lineTo(100, 100);
        RectF oval = new RectF(0, 0, 300, 300);
        path.arcTo(oval, 0, 345, true);// startAngle- 开始的角度；sweepAngel- 扫过的角度[-360, 360);
        // forceMoveTo - 是否连接圆弧的起点和Path最后操作的点。
        canvas.drawPath(path,mPaint);
    }

    private void isEmpty(Canvas canvas, Path path) {
        Log.d(TAG, "isEmpty: " + path.isEmpty());
        path.lineTo(100, 100);
        Log.d(TAG, "isEmpty: " + path.isEmpty());
        canvas.drawPath(path,mPaint);
    }

    // 判断Path是否时矩形，如果是，则将矩形的信息拷贝到参数rect中
    private void isRec(Canvas canvas, Path path) {
        path.addRect(-200, -200, 200, 200, Path.Direction.CW);
        RectF rect = new RectF();
        boolean isRect = path.isRect(rect);
        Log.d(TAG, "isRec:" + isRect + "| left:"+rect.left+"| top:"+rect.top+"| right:"+rect.right+"| bottom:"+rect.bottom);
        canvas.drawPath(path,mPaint);
    }

    private void setPath(Canvas canvas, Path path) {
        canvas.scale(1, -1);
        path.addRect(-200, -200, 200, 200, Path.Direction.CW);
        Path src = new Path();
        src.addCircle(0, 0, 100, Path.Direction.CW);
        path.set(src);
        canvas.drawPath(path,mPaint);
    }

    /**
     * 将Path 平移到指定的位置，将平移位置后的Path拷贝到参数src中，操作不会影响Path。
     */
    private void offset(Canvas canvas, Path path) {
        Log.d(TAG, "offset: ");
        canvas.scale(1, -1);
        path.addCircle(0, 0, 100, Path.Direction.CW);

        Path dst = new Path();
        dst.addRect(-200,-200,200,200, Path.Direction.CW);

        path.offset(300, 0, dst);

        canvas.drawPath(path, mPaint);

        mPaint.setColor(Color.BLUE);

        canvas.drawPath(dst, mPaint);
    }

    private void rLineTo(Canvas canvas, Path path) {
        canvas.restore();
        path.moveTo(200, 100);//移动下一次操作的起点
        path.rLineTo(200, 200); // 连接当前Point和相对当前Point偏移指定坐标的Point
        canvas.drawPath(path,mPaint);
    }

    private void computeBounds(Canvas canvas, Path path) {
        mPaint.setStrokeWidth(6);
        RectF rect1 = new RectF();
        mPaint.setColor(Color.BLACK);

        path.lineTo(100,-50);
        path.lineTo(100,50);
        path.close();
        path.addCircle(-100,0,100, Path.Direction.CW);

        path.computeBounds(rect1,true);         // 测量Path

        canvas.drawPath(path,mPaint);    // 绘制Path

        mPaint.setColor(Color.RED);
        canvas.drawRect(rect1,mPaint);   // 绘制边界
    }
}
