package com.jie.livedata

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView
import androidx.activity.viewModels

/**
 *  https://juejin.cn/post/6975689084173811743
 *
 *  observeForever也是LifecycleOwner订阅LiveData的方法，当处于inactive状态的时候，也会收到
 *  数据变化的通知。
 */
class CountDownActivity : AppCompatActivity() {
    private val countDownModel by viewModels<CountDownModel>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_count_down)
        val tvContent = findViewById<TextView>(R.id.tv_content)
        countDownModel.countDownLiveData.observe(this) {
            tvContent.text = it
        }
    }
}