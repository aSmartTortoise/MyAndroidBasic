package com.jie.databinding;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.databinding.ViewDataBinding;

import android.os.Bundle;

import com.jie.databinding.databinding.ActivityObservableFieldBinding;
import com.jie.databinding.model.ObservableGoods;

import java.lang.ref.WeakReference;
import java.util.Random;

public class ObservableFieldActivity extends AppCompatActivity {
    private ObservableGoods mGoods;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityObservableFieldBinding fieldBinding = DataBindingUtil.setContentView(this, R.layout.activity_observable_field);
        mGoods = new ObservableGoods("dogeCoin", "wa, wonderfull coin", 0.15F);
        fieldBinding.setGoods(mGoods);
        fieldBinding.setDataHandler(new DataHandler(this));
    }

    public ObservableGoods getGoods() {
        return mGoods;
    }

    public static class DataHandler {
        private WeakReference<ObservableFieldActivity> mReference;
        public DataHandler(ObservableFieldActivity activity) {
            mReference = new WeakReference<>(activity);
        }

        public void changeGoodsName() {
            if (mReference != null) {
                ObservableFieldActivity activity = mReference.get();
                ObservableGoods goods = activity.getGoods();
                goods.setName("DogeCoin super" + new Random().nextInt(100));
            }
        }

        public void changeGoodsDetails() {
            if (mReference != null) {
                ObservableFieldActivity activity = mReference.get();
                ObservableGoods goods = activity.getGoods();
                goods.setDetails("In 2022, Elon Musk said: DogeCoin can by some Tesla Belonings." + new Random().nextInt(100));
            }
        }
    }
}