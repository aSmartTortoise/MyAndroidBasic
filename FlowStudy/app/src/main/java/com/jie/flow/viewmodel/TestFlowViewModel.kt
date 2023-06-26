package com.jie.flow.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class TestFlowViewModel : ViewModel() {
    companion object {
        private const val TAG = "TestFlowViewModel"
    }
    private val _state: MutableStateFlow<Int> = MutableStateFlow(0)
    val mState: StateFlow<Int>
        get() = _state
    private val _name: MutableStateFlow<String> = MutableStateFlow("第二个StateFlow")
    val mName: MutableStateFlow<String>
        get() = _name
    private val _shared: MutableSharedFlow<Int> = MutableSharedFlow(2)
    val mShareFlow: SharedFlow<Int>
        get() = _shared

    fun downloadByStateFlow() {
        for (element in 0..5) {
            viewModelScope.launch(Dispatchers.IO) {
                delay(200L * element)
                Log.d(TAG, "downloadByStateFlow: wyj element:$element")
                _state.value = element
            }
        }
    }

    fun downloadBySharedFlow() {
        for (element in 0..5) {
            viewModelScope.launch(Dispatchers.IO) {
                delay(100L * element)
                _shared.emit(element)
            }
        }
    }
}