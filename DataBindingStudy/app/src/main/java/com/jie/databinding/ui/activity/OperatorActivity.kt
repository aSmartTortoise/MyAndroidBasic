package com.jie.databinding.ui.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.databinding.DataBindingUtil
import com.jie.databinding.R
import com.jie.databinding.databinding.ActivityOperatorBinding
import com.jie.databinding.model.UserEntity

class OperatorActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        DataBindingUtil.setContentView<ActivityOperatorBinding>(
            this,
            R.layout.activity_operator
        ).apply {
            userInfo = UserEntity("frankie", "123456")
        }
        title = "Operator study"
    }
}