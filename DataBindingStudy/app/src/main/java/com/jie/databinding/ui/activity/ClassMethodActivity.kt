package com.jie.databinding.ui.activity

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import com.jie.databinding.R
import com.jie.databinding.databinding.ActivityClassMethodBinding
import com.jie.databinding.model.UserEntity

class ClassMethodActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        DataBindingUtil.setContentView<ActivityClassMethodBinding>(
            this,
            R.layout.activity_class_method
        ).apply {
            val userInfo: UserEntity = UserEntity("FrankieAngelica", "123654")
            this.userInfo = userInfo
            val userPresenter = UserPresenter(this@ClassMethodActivity)
            this.userPresenter = userPresenter
        }
        title = "class method study"
    }

    inner class UserPresenter constructor(val context: Context) {
        fun onUserNameClick(userInfo: UserEntity) {
            Toast.makeText(context, "用户名:${userInfo.name}", Toast.LENGTH_SHORT).show()
        }
    }
}