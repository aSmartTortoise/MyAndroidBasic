package com.jie.databinding;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.content.Intent;
import android.os.Bundle;

import com.jie.databinding.databinding.ActivityMainBinding;
import com.jie.databinding.model.User;
import com.jie.databinding.ui.activity.ObservableFieldActivity;

import java.lang.ref.WeakReference;

/**
 *  https://juejin.cn/post/6844903609079971854#heading-19
 *
 *  1 单向数据绑定
 *      实现数据变化自动驱动UI刷新的方式有：BaseObservable、ObservableField、ObservableCollection。
 *      一个Model类的对象数据变化之后，并不会让UI自动刷新，通过ViewDataBinding类关联了Model和layout之后，
 *  每当Modle发生变化，自然希望对应的View能刷新。Observable就是为此而生的。
 *
 *  2 双向绑定
 *      当数据改变时驱动UI刷新，当UI改变时候，驱动数据改变。
 *  3 事件绑定
 *      事件绑定也是一种变量绑定，只不过设置的变量是回调接口而已，事件绑定可以用于以下多种回调
 *  事件：
 *      android:onClick
 *      android:onLongClick
 *      android:afterTextChanged
 *      android:onTextChanged
 *  4 BindingAdapter
 *      DataBinding提供了注解@BindingAdapter支持自定义属性或者修改原生属性。注解值可以是已有的xml属性，
 *  比如 android:src、android:text等，也可以自定义属性，在xml中使用。
 *  5 BindingConversion
 *      注解@BindingConversion可以将xml 空间属性的数据类型转换为其他数据类型。
 *  6 Array、List、Set、Map...
 *      DataBinding支持在布局文件中使用Array、List、Set、Map...，且在布局文件中都可以使用list[index]
 *  的形式来获取元素。
 *      为了和标签Variable的尖括号区分开，在声明List<String>之类的数据类型时，需要使用尖括号的转移字符。
 *      左尖括号 <   ---> &lt;
 *      右尖括号 >   ---> &gt;
 */
public class MainActivity extends AppCompatActivity {
    private User mUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityMainBinding activityMainBinding = DataBindingUtil.setContentView(
                this, R.layout.activity_main);
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
        }

        public void toObservableField() {
            if (mReference != null) {
                MainActivity activity = mReference.get();
                activity.startActivity(new Intent(activity, ObservableFieldActivity.class));
            }
        }

        public void toObservableCollection() {
            if (mReference != null) {
//                MainActivity activity = mReference.get();
//                activity.startActivity(new Intent(activity, ObservableCollectionActivity2.class));
            }
        }

        public void toBothWayBinding() {
//            if (mReference != null) {
//                MainActivity activity = mReference.get();
//                activity.startActivity(new Intent(activity, BothWayBindingActivity.class));
//            }
        }

        public void toEventBinding() {
            if (mReference != null) {
//                MainActivity activity = mReference.get();
//                activity.startActivity(new Intent(activity, EventBindingActivity.class));
            }
        }

        public void toClassMethod() {
            if (mReference != null) {
//                MainActivity activity = mReference.get();
//                activity.startActivity(new Intent(activity, ClassMethodActivity.class));
            }
        }

        public void toOperator() {
            if (mReference != null) {
//                MainActivity activity = mReference.get();
//                activity.startActivity(new Intent(activity, OperatorActivity.class));
            }
        }

        public void toIncludeViewStub() {
//            if (mReference != null) {
//                MainActivity activity = mReference.get();
//                activity.startActivity(new Intent(activity, InclueViewStubActivity.class));
//            }
        }

        public void toViewStub2() {
//            if (mReference != null) {
//                MainActivity activity = mReference.get();
//                activity.startActivity(new Intent(activity, ViewStubActivity2.class));
//            }
        }

        public void toBindingAdapter() {
            if (mReference != null) {
                MainActivity activity = mReference.get();
                activity.startActivity(new Intent(activity, BindingAdapterConversionActivity.class));
            }
        }

        public void toCollection() {
            if (mReference != null) {
                MainActivity activity = mReference.get();
                activity.startActivity(new Intent(activity, CollectionActivity.class));
            }
        }

        public void toRecyclerView() {
            if (mReference != null) {
                MainActivity activity = mReference.get();
                activity.startActivity(new Intent(activity, RecyclerActivity.class));
            }
        }
    }
}