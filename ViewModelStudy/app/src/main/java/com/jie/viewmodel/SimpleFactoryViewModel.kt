package com.jie.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel

class SimpleFactoryViewModel : ViewModel() {
    fun print() {
        Log.d("SimpleFactoryViewModel", "print: wyj 简单工厂创建ViewModle")
    }
}