package com.jie.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class MyViewModel : ViewModel() {
    private var mName: MutableLiveData<String>? = null
    public fun getName(): LiveData<String>? {
        if (mName == null) {
            mName = MutableLiveData()
            addName()
        }
        return mName
    }

    private fun addName() {
        mName?.value = "Android Jetpack architechture component: ViewModel."
    }
}