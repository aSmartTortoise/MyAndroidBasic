package com.jie.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel

class CustomSimpleViewModel(private val mData: String) : ViewModel() {
    fun print() {
        Log.d("CustpmSimpleViewModel", "print: wyj mData:$mData")
    }
}