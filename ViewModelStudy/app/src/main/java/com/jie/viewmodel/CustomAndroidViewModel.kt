package com.jie.viewmodel

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel

class CustomAndroidViewModel(app: Application, private val mData: String) : AndroidViewModel(app) {
    fun print() {
        Log.d("CustomAndroidViewModel", "print: wyj mData:$mData")
    }
}