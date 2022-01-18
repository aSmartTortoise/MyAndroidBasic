package com.jie.databinding;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.os.Bundle;

import com.jie.databinding.databinding.ActivityOperatorBinding;
import com.jie.databinding.model.User;

public class OperatorActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityOperatorBinding operatorBinding = DataBindingUtil.setContentView(this, R.layout.activity_operator);
        User user = new User("frankie", "123456");
        operatorBinding.setUserInfo(user);
        setTitle("operator 运算符");
    }
}