package com.jie.viewmodel

import android.os.CountDownTimer
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class ShareDataModel : ViewModel() {
    val mLiveData = MutableLiveData<String>()
    var total = 2000L
    init {
        val countDownTimer = object : CountDownTimer(1000 * total, 1000L) {
            override fun onTick(millisUntilFinished: Long) {
                mLiveData.postValue("剩余时间：${--total}")
            }

            override fun onFinish() {
                Log.d("ShareDataModel", "onFinish: wyj 倒计时完成")
            }
        }

        countDownTimer.start()
    }
}