package com.jie.databinding;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.databinding.Observable;

import com.jie.databinding.databinding.ActivityBaseobservableBinding;
import com.jie.databinding.model.Goods;

import android.os.Bundle;
import android.util.Log;


import java.util.Random;

public class BaseObservableActivity extends AppCompatActivity {
    private final String TAG = "SecondActivity";
    private Goods mGoods;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityBaseobservableBinding baseobservableBinding = DataBindingUtil.setContentView(this, R.layout.activity_baseobservable);
        mGoods = new Goods("dogecoin", "buy somethings", 0.25F);
        baseobservableBinding.setGoods(mGoods);
        baseobservableBinding.setGoodsHandler(new GoodsHandler());
        mGoods.addOnPropertyChangedCallback(new Observable.OnPropertyChangedCallback() {
            @Override
            public void onPropertyChanged(Observable sender, int propertyId) {
                if (propertyId == BR.name) {
                    Log.d(TAG, "onPropertyChanged: wyj goods name changes");
                } else if (propertyId == BR.details) {
                    Log.d(TAG, "onPropertyChanged: wyj goods details changed.");
                } else if (propertyId == BR._all) {
                    Log.d(TAG, "onPropertyChanged: wyj goods all property changed.");
                } else {
                    Log.d(TAG, "onPropertyChanged: wyj unknow.");
                }
            }
        });
    }

    public class GoodsHandler {
        public void changeGoodsName() {
            mGoods.setName("dogeCoin super" + new Random().nextInt(100));
            mGoods.setPrice(new Random().nextFloat());
        }

        public void changeGoodDetails() {
            mGoods.setDetails("in 2022, tesla goods can be buy with dogeCoin, said Elon Musk."+ new Random().nextInt(100));
            mGoods.setPrice(new Random().nextFloat());
        }
    }
}