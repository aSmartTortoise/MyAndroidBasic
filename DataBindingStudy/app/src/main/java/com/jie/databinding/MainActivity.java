package com.jie.databinding;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.databinding.ViewDataBinding;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.jie.databinding.databinding.ActivityMainBinding;
import com.jie.databinding.model.User;

/**
 *  https://juejin.cn/post/6844903609079971854#heading-19
 *
 *  1 单向数据绑定
 *      实现数据变化自动驱动UI刷新的方式有：BaseObservable、ObservableField、ObservableCollection。
 *
 */
public class MainActivity extends AppCompatActivity {
    private User mUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityMainBinding activityMainBinding = DataBindingUtil.setContentView(this, R.layout.activity_main);
        mUser = new User("frankie", "123456");
        activityMainBinding.setUserInfo(mUser);
        activityMainBinding.btnSecond.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this, SecondActivity.class));
            }
        });

    }
}