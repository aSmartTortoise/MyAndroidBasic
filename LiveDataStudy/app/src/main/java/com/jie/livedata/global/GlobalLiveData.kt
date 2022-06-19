package com.jie.livedata.global

import androidx.lifecycle.LiveData

class GlobalLiveData : LiveData<String>() {
    val countdownManager = CountdownManager()
    val listener = object : CountdownManager.OnDataChangeListener {
        override fun onChanged(data: String) {
            postValue(data)
        }
    }

    companion object {
        private lateinit var globalLiveData: GlobalLiveData
        fun getInstance(): GlobalLiveData {
            globalLiveData = if (::globalLiveData.isInitialized) globalLiveData else GlobalLiveData()
            return globalLiveData
        }
    }

    override fun onActive() {
        super.onActive()
        countdownManager.addListener(listener)
    }

    override fun onInactive() {
        super.onInactive()
        countdownManager.removeListener(listener)
    }


}