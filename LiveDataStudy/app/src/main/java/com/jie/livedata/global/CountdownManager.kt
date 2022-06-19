package com.jie.livedata.global

import android.os.CountDownTimer

class CountdownManager {
    private var remainTime = 2000L
    private val listeners = mutableListOf<OnDataChangeListener>()

    init {
        object : CountDownTimer(2000L * 1000L, 1000L) {
            override fun onTick(millisUntilFinished: Long) {
                remainTime--
                callback("剩余 $remainTime 秒")
            }

            override fun onFinish() {
                callback("倒计时结束")
            }
        }.apply { start() }
    }

    private fun callback(msg: String) {
        for (listener in listeners) {
            listener.onChanged(msg)
        }
    }

    fun addListener(listener: OnDataChangeListener) {
        listeners.add(listener)
    }

    fun removeListener(listener: OnDataChangeListener) {
        listeners.remove(listener)
    }

    interface OnDataChangeListener {
        fun onChanged(data: String)
    }
}