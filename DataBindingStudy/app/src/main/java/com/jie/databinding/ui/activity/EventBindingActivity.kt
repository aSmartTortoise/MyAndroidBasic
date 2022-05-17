package com.jie.databinding.ui.activity

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import com.jie.databinding.R
import com.jie.databinding.databinding.ActivityEventBindingBinding
import com.jie.databinding.model.UserEntity

class EventBindingActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val eventBinding = DataBindingUtil.setContentView<ActivityEventBindingBinding>(
            this,
            R.layout.activity_event_binding
        )
        var userInfo: UserEntity = UserEntity("Jack", "123456")
        eventBinding.userInfo = userInfo
        UserPresenter(this, userInfo, eventBinding).apply {
            eventBinding.userPresenter = this
        }
        title = "event binding study"
    }

    inner class UserPresenter constructor(
        val context: Context,
        var userInfo: UserEntity,
        val eventBinding: ActivityEventBindingBinding) {

        fun onUserNameClick(userInfo: UserEntity) {
            Toast.makeText(context, userInfo.name, Toast.LENGTH_SHORT).show()
        }

        fun afterTextChanged(editable: Editable) {
            userInfo.name = editable.toString()
            eventBinding.userInfo = userInfo
        }

        fun afterUserPwdChanged(editable: Editable) {
            userInfo.pwd = editable.toString()
            eventBinding.userInfo = userInfo
        }
    }
}