package com.wyj.view.animation;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.graphics.PointF;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.wyj.view.R;

public class ShoppingActivity extends AppCompatActivity implements ShoppingView.AnimatorListener {
    private static final String TAG = "ShippingActivity";

    private ImageView mIvGoods;
    private View mTvAdd;
    private ImageView mIvShoppingCar;
    private TextView mTvNum;

    private ObjectAnimator scaleXAnim;
    private ObjectAnimator scaleYAnim;

    private AnimatorSet animSet;

    private int count = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shipping);
        mIvGoods = findViewById(R.id.iv_icon);
        mTvAdd = findViewById(R.id.tv_add);
        mIvShoppingCar = findViewById(R.id.iv_shopping_cart);
        mTvNum = findViewById(R.id.tv_dot);
        scaleXAnim = ObjectAnimator.ofFloat(mTvNum, "scaleX", 1f, 1.2f, 1f, 1.1f, 1f);
        scaleYAnim = ObjectAnimator.ofFloat(mTvNum, "scaleY", 1f, 1.2f, 1f, 1.1f, 1f);
        animSet = new AnimatorSet();
        animSet.play(scaleXAnim).with(scaleYAnim);
        mTvAdd.setOnClickListener(view -> {
            PointF startPoint = getPointOnScreen(mIvGoods);
            PointF endPoint = getPointOnScreen(mIvShoppingCar);
            ShoppingView shoppingView = new ShoppingView(ShoppingActivity.this);
            shoppingView.setListener(this);
            shoppingView.setImageDrawable(mIvGoods.getDrawable());
            ViewGroup.LayoutParams layoutParams = new ViewGroup.LayoutParams(mIvGoods.getWidth(),
                    mIvGoods.getHeight());
            ViewGroup decorView = (ViewGroup) getWindow().getDecorView();
            decorView.addView(shoppingView, layoutParams);
            shoppingView.start(startPoint, endPoint);
        });
    }

    private PointF getPointOnScreen(View view) {
        int[] locations = new int[2];
        view.getLocationOnScreen(locations);
        Log.d(TAG, "getPointOnScreen: x:" + locations[0] + " y:" + locations[1]);
        return new PointF(locations[0], locations[1]);
    }

    @Override
    public void animEnd() {
        addCount();
        mIvShoppingCar.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.shopping_cart_full));
    }

    public void addCount() {
        ++count;
        if (count <= 0) {
            return;
        }

        if(animSet.isRunning()){
            animSet.cancel();
        }

        animSet.start();

        mTvNum.setVisibility(View.VISIBLE);
        mTvNum.setText(count + "");
    }
}