package com.jie.databinding;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.os.Bundle;
import android.text.Editable;
import android.widget.Toast;

import com.jie.databinding.databinding.ActivityEventBindingBinding;
import com.jie.databinding.model.User;

import java.lang.ref.WeakReference;

public class EventBindingActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityEventBindingBinding eventBindingBinding = DataBindingUtil.setContentView(this, R.layout.activity_event_binding);
        setTitle("event binding");
        User userInfo = new User("Jack", "123456");
        eventBindingBinding.setUserInfo(userInfo);
        UserPresenter presenter = new UserPresenter(eventBindingBinding, userInfo);
        eventBindingBinding.setUserPresenter(presenter);
    }

    public class UserPresenter {
        private ActivityEventBindingBinding mEventBindingBinding;
        private User mUser;

        public UserPresenter(ActivityEventBindingBinding eventBindingBinding, User user) {
            mEventBindingBinding = eventBindingBinding;
            mUser = user;
        }

        public void onUserNameClick(User user) {
            Toast.makeText(EventBindingActivity.this, "用户名:" + user.getName(), Toast.LENGTH_LONG).show();
        }

        public void afterTextChanged(Editable e) {
            mUser.setName(e.toString());
            mEventBindingBinding.setUserInfo(mUser);
        }

        public void afterUserPwdChanged(Editable e) {
            mUser.setPwd(e.toString());
            mEventBindingBinding.setUserInfo(mUser);
        }
    }
}