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

    fun download() {
        for (state in 0..5) {
            viewModelScope.launch(Dispatchers.IO) {
                delay(200L * state)
                _state.value = state
            }
        }
    }
}