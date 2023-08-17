package com.wyj.view.timeline;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.support.v7.widget.RecyclerView;
import android.view.View;

public class LogisticsItemDecoration extends RecyclerView.ItemDecoration {
    private static final String TAG = "LogisticsItemDecoration";

    //点
    private Paint mDotPaint;
    private Paint mLinePaint;
    // 左 上偏移量
    private int itemViewTopOffsets;

    // 点半径
    private int mRadius;
    // 点的上顶点与itemview top的偏移量。
    private int mDotItemViewTopOffsets;
    // 点左侧偏移、线两边总偏移，线宽。
    private int mLineOffsets, mLineWidth, mLeftOffsets;

    public LogisticsItemDecoration(Builder builder) {
        mDotPaint = builder.mDotPaint;
        mLinePaint = builder.mLinePaint;
        itemViewTopOffsets = builder.itemViewTopOffsets;
        mRadius = builder.mRadius;
        mDotItemViewTopOffsets = builder.mDotItemViewTopOffsets;
        mLineWidth = builder.mLineWidth;
        mLineOffsets = builder.mLineOffsets;
        mLeftOffsets = builder.mLeftOffsets;
    }


    public static class Builder {
        private Paint mDotPaint;
        private Paint mLinePaint;
        private int itemViewTopOffsets;
        private int mRadius;
        private int mDotItemViewTopOffsets;
        private int mLineOffsets, mLineWidth, mLeftOffsets;
        public Builder() {
        }

        public Builder setDot(int radius, int dotColor, int dotItemViewTopOffsets) {
            mDotPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
            mDotPaint.setColor(dotColor);
            mRadius = radius;
            mDotItemViewTopOffsets = dotItemViewTopOffsets;
            return this;
        }

        public Builder setLine(int lineWidth, int lineOffsets, int lineColor) {
            mLinePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
            mLinePaint.setStrokeWidth(lineWidth);
            mLinePaint.setColor(lineColor);
            mLineWidth = lineWidth;
            mLineOffsets = lineOffsets;
            return this;
        }

        public Builder setOffsets(int leftOffsets, int topOffsets) {
            mLeftOffsets = leftOffsets;
            itemViewTopOffsets = topOffsets;
            return this;
        }

        public LogisticsItemDecoration build() {
            return new LogisticsItemDecoration(this);
        }
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        super.getItemOffsets(outRect, view, parent, state);
        outRect.set(mLeftOffsets + mLineOffsets + mLineWidth, itemViewTopOffsets, 0, 0);
    }

    @Override
    public void onDraw(Canvas c, RecyclerView parent, RecyclerView.State state) {
        super.onDraw(c, parent, state);
        RecyclerView.Adapter adapter = parent.getAdapter();
        int itemCount = parent.getChildCount();
        if (itemCount <= 0) return;
        for (int index = 0; index < itemCount; index++) {
            View child = parent.getChildAt(index);

            if (child != null) {
                RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) child.getLayoutParams();
                int pos = params.getViewAdapterPosition();
                int centerX = parent.getPaddingLeft() + mLeftOffsets + mRadius;
                int centerY = child.getTop() + mRadius + mDotItemViewTopOffsets;
                // 绘点
                c.drawCircle(centerX, centerY, mRadius, mDotPaint);

                /**
                 * 绘制上半轴线
                 */
                int upLineStartX = centerX;
                int upLineStartY = child.getTop() - itemViewTopOffsets;

                int upLineStopX = centerX;
                int upLineStopY = centerY - mRadius;

                if (pos > 0) {
                    c.drawLine(upLineStartX, upLineStartY, upLineStopX, upLineStopY, mLinePaint);
                }

                /**
                 * 绘制下半轴线
                 */

                int bottomLineStartX = centerX;
                int bottomLineStartY = centerY + mRadius;

                int bottomLineStopX = centerX;
                int bottomLineStopY = child.getBottom();

                if (pos < adapter.getItemCount() - 1) {
                    c.drawLine(bottomLineStartX, bottomLineStartY, bottomLineStopX, bottomLineStopY, mLinePaint);
                }
            }
        }
    }
}
