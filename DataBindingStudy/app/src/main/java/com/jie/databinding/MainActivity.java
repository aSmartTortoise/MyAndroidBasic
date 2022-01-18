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
 *  2 双向绑定
 *      当数据改变时驱动UI刷新，当UI改变时候，驱动数据改变。
 *  3 事件绑定
 *      事件绑定也是一种变量绑定，只不过设置的变量是回调接口而已，事件绑定可以用于以下多种回调
 *  事件：
 *      android:onClick
 *      android:onLongClick
 *      android:afterTextChanged
 *      android:onTextChanged
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

        public void toEventBinding() {
            if (mReference != null) {
                MainActivity activity = mReference.get();
                activity.startActivity(new Intent(activity, EventBindingActivity.class));
            }
        }

        public void toClassMethod() {
            if (mReference != null) {
                MainActivity activity = mReference.get();
                activity.startActivity(new Intent(activity, ClassMethodActivity.class));
            }
        }

        public void toOperator() {
            if (mReference != null) {
                MainActivity activity = mReference.get();
                activity.startActivity(new Intent(activity, OperatorActivity.class));
            }
        }

        public void toIncludeViewStub() {
            if (mReference != null) {
                MainActivity activity = mReference.get();
                activity.startActivity(new Intent(activity, InclueViewStubActivity.class));
            }
        }
    }
}