package com.jie.flow

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.activity.viewModels
import androidx.lifecycle.lifecycleScope
import com.jie.flow.viewmodel.TestFlowViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
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
//            sharedFlowImpReplayStudy()
            sharedFlowReplayCacheStudy()

        }
        viewModel.downloadBySharedFlow()
    }

    /**
     *  当有数据变化的时候，SharedFlow会把新的数据存到buffer中。当有新的订阅者时，优先从buffer中获取值，
     *  获取的replayCache只是指定replay大小的片段
     */
    private suspend fun sharedFlowReplayCacheStudy() {
        var index = 0
        viewModel.mShareFlow.collect {
            Log.d(
                TAG,
                "onCreate: wyj 第${index++}次变化，element:$it, replayCache:${viewModel.mShareFlow.replayCache}"
            )
        }
    }

    private fun CoroutineScope.sharedFlowImpReplayStudy() {
        launch {
            viewModel.mShareFlow.collect {
                Log.d(TAG, "onCreate: wyj shared first launch element:$it")
            }
        }

        launch {
            delay(3000L)
            viewModel.mShareFlow.collect {
                Log.d(TAG, "onCreate: wyj shared second launch element:$it")
            }
        }
    }

    override fun onDestroy() {
        lifecycleScope.cancel()
        super.onDestroy()
    }
}