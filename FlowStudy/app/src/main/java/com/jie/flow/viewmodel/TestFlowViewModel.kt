package com.jie.flow.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class TestFlowViewModel : ViewModel() {
    private val _state: MutableStateFlow<Int> = MutableStateFlow(0)
    val mState: StateFlow<Int>
        get() = _state
    private val _name: MutableStateFlow<String> = MutableStateFlow("第二个StateFlow")
    val mName: MutableStateFlow<String>
        get() = _name

    fun download() {
        for (element in 0..5) {
            viewModelScope.launch(Dispatchers.IO) {
                delay(200L * element)
                _state.value = element
            }
        }
    }
}