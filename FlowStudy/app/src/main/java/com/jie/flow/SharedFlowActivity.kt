package com.jie.flow

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.activity.viewModels
import androidx.lifecycle.lifecycleScope
import com.jie.flow.viewmodel.TestFlowViewModel
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class SharedFlowActivity : AppCompatActivity() {
    companion object {
        const val TAG = "SharedFlowActivity"
    }
    private val viewModel: TestFlowViewModel by viewModels()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_shared_flow)
        lifecycleScope.launch {
            viewModel.mShareFlow.collect {
                Log.d(TAG, "onCreate: wyj shared element:$it")
            }
        }
        viewModel.downloadBySharedFlow()
    }

    override fun onDestroy() {
        lifecycleScope.cancel()
        super.onDestroy()
    }
}