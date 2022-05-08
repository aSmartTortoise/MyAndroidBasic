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

class HotFlowStudyActivity : AppCompatActivity() {
    companion object {
        const val TAG = "HotFlowStudyActivity"
    }
    private val viewModel: TestFlowViewModel by viewModels()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_hot_flow_study)
        lifecycleScope.launch {
            viewModel.mState.collect {
                Log.d(TAG, "onCreate: wyj state:$it")
            }
        }
        viewModel.download()
    }

    override fun onDestroy() {
        lifecycleScope.cancel()
        super.onDestroy()
    }
}