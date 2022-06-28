package com.jie.livedata.coroutine

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import kotlinx.coroutines.delay

class CoroutineLiveDataViewModel : ViewModel() {
    fun startAsyncWithSecond(second: Int): LiveData<String> = liveData {
        delay(second * 1000L)
        emit("倒计时结束")
    }

    fun startAsyncEmitSource(second: Int) = liveData<String> {
        delay(second * 1000L)
        emit("$second 秒阻塞完成，在阻塞$second 秒后通知你")
        val emitSourceLiveData = MutableLiveData<String>()
        emitSource(emitSourceLiveData)
        delay(second * 1000L)
        emitSourceLiveData.value = "再次阻塞$second 秒完成"
    }
}