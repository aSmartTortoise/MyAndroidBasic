package com.jie.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel

class EnTrustViewModel : ViewModel() {
    fun print() {
        Log.d("EnTrustViewModel", "print: wyj 使用委托机制获取ViewModel的实例")
    }
}