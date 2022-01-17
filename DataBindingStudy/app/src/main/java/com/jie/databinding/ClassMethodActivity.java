package com.jie.databinding;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.os.Bundle;
import android.text.Editable;
import android.widget.Toast;

import com.jie.databinding.databinding.ActivityClassMethodBinding;
import com.jie.databinding.databinding.ActivityEventBindingBinding;
import com.jie.databinding.model.User;

public class ClassMethodActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityClassMethodBinding methodBinding = DataBindingUtil.setContentView(this, R.layout.activity_class_method);
        User userinfo = new User("FrankieAngelica", "123654");
        UserPresenter userPresenter = new UserPresenter();
        methodBinding.setUserInfo(userinfo);
        methodBinding.setUserPresenter(userPresenter);
        setTitle("class method");
    }

    public class UserPresenter {
        public void onUserNameClick(User user) {
            Toast.makeText(ClassMethodActivity.this, "用户名:" + user.getName(), Toast.LENGTH_LONG).show();
        }

    }
}