package com.jie.livedata.coroutine

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.viewModels
import androidx.databinding.DataBindingUtil
import com.jie.livedata.R
import com.jie.livedata.databinding.ActivityCoroutineLiveDataBinding

class CoroutineLiveDataActivity : AppCompatActivity() {
    private val model: CoroutineLiveDataViewModel by viewModels()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val coroutineLiveDataBinding = DataBindingUtil.setContentView<ActivityCoroutineLiveDataBinding>(
            this,
            R.layout.activity_coroutine_live_data
        )
        coroutineLiveDataBinding.btnStartasync.setOnClickListener {
            model.startAsyncWithSecond(3).observe(this) {
                coroutineLiveDataBinding.btnStartasync.text = it
            }
        }
        coroutineLiveDataBinding.btnStartasyncEmitsource.setOnClickListener {
            model.startAsyncEmitSource(3).observe(this) {
                coroutineLiveDataBinding.btnStartasyncEmitsource.text = it
            }
        }
    }
}