package com.jie.databinding;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.jie.databinding.databinding.ActivityInclueViewStubBinding;
import com.jie.databinding.model.User;

public class InclueViewStubActivity extends AppCompatActivity {
    private ActivityInclueViewStubBinding mBinding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_inclue_view_stub);
        User user = new User("frankie", "123456");
        mBinding.setUseInfo(user);
        Presenter presenter = new Presenter();
        mBinding.setPresenter(presenter);
        setTitle("includeViewStub");
    }

    public class Presenter {
        public void onClick(View view) {
            Log.d("inCludeViewStub", "onClick: wyj show viewStub");

            if (!mBinding.viewStub.isInflated()) {
                mBinding.viewStub.getViewStub().inflate();
            }
        }
    }
}