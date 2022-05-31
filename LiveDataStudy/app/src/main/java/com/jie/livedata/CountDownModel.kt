package com.jie.livedata

import android.os.CountDownTimer
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class CountDownModel : ViewModel() {
    val countDownLiveData = MutableLiveData<String>()
    private var remainTime = 2000

    init {
        object : CountDownTimer(2000L * 1000, 1000L) {
            override fun onTick(millisUntilFinished: Long) {
                remainTime--
                countDownLiveData.postValue("剩余：$remainTime 秒")
            }

            override fun onFinish() {
                countDownLiveData.postValue("倒计时结束")
            }

        }.apply { start() }
    }
}