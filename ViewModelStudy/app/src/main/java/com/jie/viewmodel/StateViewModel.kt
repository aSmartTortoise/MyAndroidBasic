package com.jie.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class StateViewModel : ViewModel() {
    val countData = MutableLiveData<Int>(0)

    fun setCount(value: Int) {
        countData.postValue(value)
    }
}