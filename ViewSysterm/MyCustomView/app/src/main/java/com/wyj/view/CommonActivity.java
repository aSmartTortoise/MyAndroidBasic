package com.wyj.view;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.widget.CompoundButton;
import android.widget.RadioButton;

import com.gw.swipeback.SwipeBackLayout;
import com.wyj.view.utils.BarUtils;

/**
 * Created by GongWen on 17/8/24.
 */

public class CommonActivity extends AppCompatActivity implements CompoundButton.OnCheckedChangeListener {
    private SwipeBackLayout mSwipeBackLayout;
    private RadioButton fromLeftRb;
    private RadioButton fromTopRb;
    private RadioButton fromRightRb;
    private RadioButton fromBottomRb;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_common);
        BarUtils.transparentStatusBar(this);
        mSwipeBackLayout = findViewById(R.id.swipeBackLayout);
        fromLeftRb = (RadioButton) findViewById(R.id.fromLeftRb);
        fromLeftRb.setOnCheckedChangeListener(this);
        fromTopRb = (RadioButton) findViewById(R.id.fromTopRb);
        fromTopRb.setOnCheckedChangeListener(this);
        fromRightRb = (RadioButton) findViewById(R.id.fromRightRb);
        fromRightRb.setOnCheckedChangeListener(this);
        fromBottomRb = (RadioButton) findViewById(R.id.fromBottomRb);
        fromBottomRb.setOnCheckedChangeListener(this);
        switch (mSwipeBackLayout.getDirectionMode()) {
            case SwipeBackLayout.FROM_LEFT:
                fromLeftRb.setChecked(true);
                break;
            case SwipeBackLayout.FROM_TOP:
                fromTopRb.setChecked(true);
                break;
            case SwipeBackLayout.FROM_RIGHT:
                fromRightRb.setChecked(true);
                break;
            case SwipeBackLayout.FROM_BOTTOM:
                fromBottomRb.setChecked(true);
                break;
        }
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        if (isChecked) {
            switch (buttonView.getId()) {
                case R.id.fromLeftRb:
                    mSwipeBackLayout.setDirectionMode(SwipeBackLayout.FROM_LEFT);
                    break;
                case R.id.fromTopRb:
                    mSwipeBackLayout.setDirectionMode(SwipeBackLayout.FROM_TOP);
                    break;
                case R.id.fromRightRb:
                    mSwipeBackLayout.setDirectionMode(SwipeBackLayout.FROM_RIGHT);
                    break;
                case R.id.fromBottomRb:
                    mSwipeBackLayout.setDirectionMode(SwipeBackLayout.FROM_BOTTOM);
                    break;
            }
        }
    }
}
