package com.jie.databinding;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.os.Bundle;

import com.jie.databinding.databinding.ActivityBothWayBindingBinding;
import com.jie.databinding.model.ObservableGoods;

public class BothWayBindingActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityBothWayBindingBinding bothWayBindingBinding = DataBindingUtil.setContentView(this, R.layout.activity_both_way_binding);
        ObservableGoods goods = new ObservableGoods("DogeCoin", "DogeCoin is a wonderful crypton, you can own it", 0.19F);
        bothWayBindingBinding.setGoods(goods);
        setTitle("both-way binding");
    }
}