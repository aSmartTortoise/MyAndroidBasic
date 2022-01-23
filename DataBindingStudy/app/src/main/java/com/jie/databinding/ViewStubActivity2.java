package com.jie.databinding;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.jie.databinding.databinding.ActivityViewStub2Binding;
import com.jie.databinding.databinding.ViewStubBinding;
import com.jie.databinding.model.User;

public class ViewStubActivity2 extends AppCompatActivity {
    private ActivityViewStub2Binding mViewStubBinding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mViewStubBinding = DataBindingUtil.setContentView(this, R.layout.activity_view_stub2);
        User user = new User("frankie", "123456");
        mViewStubBinding.setPresenter(new Presenter());
        mViewStubBinding.viewStub.setOnInflateListener((stub, inflated) -> {
            ViewStubBinding viewStubBinding = DataBindingUtil.bind(inflated);
            viewStubBinding.setUserInfo(user);
        });
        setTitle("ViewStub onInflateListener");
    }

    public class Presenter {
        public void onClick(View view) {
            Log.d("inCludeViewStub", "onClick: wyj show viewStub");

            if (!mViewStubBinding.viewStub.isInflated()) {
                mViewStubBinding.viewStub.getViewStub().inflate();
            }
        }
    }
}