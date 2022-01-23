package com.jie.databinding;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.os.Bundle;

import com.jie.databinding.databinding.ActivityBindingAdapterConversionBinding;
import com.jie.databinding.model.Image;

import java.util.Random;


public class BindingAdapterConversionActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityBindingAdapterConversionBinding adapterBinding = DataBindingUtil.setContentView(this, R.layout.activity_binding_adapter_conversion);
        adapterBinding.setImageInfo(new Image("baidu"));
        adapterBinding.setPresenter(new Presenter());
        setTitle("BindingAdapter");
    }

    public class Presenter {
        public boolean onClick(Image image) {
            image.getUrl().set("xxxJackie" + new Random().nextInt(1000));
            return true;
        }
    }
}