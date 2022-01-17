package com.jie.databinding;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.content.Intent;
import android.os.Bundle;

import com.jie.databinding.databinding.ActivityMainBinding;
import com.jie.databinding.model.User;

import java.lang.ref.WeakReference;

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
        activityMainBinding.setDataHandler(new DataHandler(this));
    }

    public static class DataHandler {
        private WeakReference<MainActivity> mReference;
        public DataHandler(MainActivity activity) {
            mReference = new WeakReference<>(activity);
        }

        public void toBaseObservalbe() {
            if (mReference != null) {
                MainActivity activity = mReference.get();
                activity.startActivity(new Intent(activity, BaseObservableActivity.class));
            }
        }

        public void toObservableField() {
            if (mReference != null) {
                MainActivity activity = mReference.get();
                activity.startActivity(new Intent(activity, ObservableFieldActivity.class));
            }
        }

        public void toObservableCollection() {
            if (mReference != null) {
                MainActivity activity = mReference.get();
                activity.startActivity(new Intent(activity, ObservableCollectionActivity.class));
            }
        }

        public void toBothWayBinding() {
            if (mReference != null) {
                MainActivity activity = mReference.get();
                activity.startActivity(new Intent(activity, BothWayBindingActivity.class));
            }
        }
    }
}