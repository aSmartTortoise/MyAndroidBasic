package com.wyj.view.animation;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.TypeEvaluator;
import android.content.Context;
import android.graphics.PointF;
import androidx.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.LinearInterpolator;

import com.wyj.view.utils.BezierUtils;

import java.util.ArrayList;
import java.util.List;

public class GoodsView extends androidx.appcompat.widget.AppCompatImageView {
    private static final String TAG = "ShoppingView";
    private AnimatorSet mAnimatorSet;
    // 缩放动画
    private ObjectAnimator mScaleXAnimator;
    private ObjectAnimator mScaleYAnimator;
    // 旋转动画
    private ObjectAnimator mRotateAnimator;
    // 移动动画
    private ObjectAnimator mTranslateAnimator;
    private AnimatorListener mListener;
    public GoodsView(Context context) {
        this(context, null);
    }

    public GoodsView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public GoodsView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        mAnimatorSet = new AnimatorSet();
        // X、Y 均缩小0.2倍
        mScaleYAnimator = ObjectAnimator.ofFloat(this, "scaleY", 1f, 0.8f);
        mScaleYAnimator.setDuration(200);
        mScaleYAnimator.setInterpolator(new AccelerateInterpolator());

        mScaleXAnimator = ObjectAnimator.ofFloat(this, "scaleX", 1f, 0.8f);
        mScaleXAnimator.setDuration(200);
        mScaleXAnimator.setInterpolator(new AccelerateInterpolator());

        // 顺时针旋转 35 度
        mRotateAnimator = ObjectAnimator.ofFloat(this, "rotation", 0, 35);
        mRotateAnimator.setDuration(100);
        mRotateAnimator.setInterpolator(new LinearInterpolator());
    }

    public void setListener(AnimatorListener listener) {
        this.mListener = listener;
    }

    /**
     * 设置坐标
     *
     * @param position View的位置 PointF
     */
    public void setPosition(PointF position) {
        if (position == null) {
            return;
        }

        Log.d(TAG, "setPosition: " + position.toString());

        setTranslationX(position.x);
        setTranslationY(position.y);
    }

    public void start(PointF startPoint, PointF endPoint) {
        setPosition(startPoint);

        mTranslateAnimator = ObjectAnimator.ofObject(this,
                "position",
                new BezierEvaluator(startPoint, endPoint),
                startPoint,
                endPoint);

        mTranslateAnimator.setDuration(450);
        mTranslateAnimator.setInterpolator(new AccelerateInterpolator());

        mAnimatorSet.play(mScaleYAnimator)
                .with(mScaleXAnimator)
                .before(mRotateAnimator);
        mAnimatorSet.play(mRotateAnimator)
                .before(mTranslateAnimator);
        mAnimatorSet.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                if (mListener != null) {
                    mListener.animEnd();
                }

                // 将自己移除
                ViewGroup parent = (ViewGroup) GoodsView.this.getParent();
                if (parent != null && parent instanceof ViewGroup) {
                    parent.removeView(GoodsView.this);
                }

            }
        });

        mAnimatorSet.start();
    }

    /**
     * 动画回调
     */
    public interface AnimatorListener {
        /**
         * 动画结束
         */
        void animEnd();
    }

    public static class BezierEvaluator implements TypeEvaluator<PointF> {
        private List<PointF> mPoints;

        public BezierEvaluator(PointF startPoint, PointF endPoint) {
            mPoints = new ArrayList<>();
            mPoints.add(startPoint);
            PointF controlPoint = new PointF(endPoint.x, startPoint.y);
            mPoints.add(controlPoint);
            mPoints.add(endPoint);
        }

        @Override
        public PointF evaluate(float fraction, PointF startValue, PointF endValue) {
            // 由贝塞尔曲线上的数据点和控制点，求曲线上任意一点的坐标。
            return new PointF(
                    BezierUtils.calculatePointCoordinate(BezierUtils.X_TYPE, fraction, 2, 0, mPoints),
                    BezierUtils.calculatePointCoordinate(BezierUtils.Y_TYPE, fraction, 2, 0, mPoints)
            );
        }
    }
}
