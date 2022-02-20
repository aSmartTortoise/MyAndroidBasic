package com.jie.viewmodel

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel

class AndroidFactoryViewModel(app: Application) : AndroidViewModel(app) {
    fun print() {
        Log.d("AndroidFactoryViewModel", "print: wyj 使用Android工厂创建ViewModel.")
    }
}