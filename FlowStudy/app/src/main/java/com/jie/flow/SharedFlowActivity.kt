package com.jie.flow

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.activity.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.jie.flow.viewmodel.TestFlowViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.shareIn
import kotlinx.coroutines.launch

/**
 *  https://juejin.cn/post/7046156485407014920/
 */
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
//            sharedFlowReplayCacheStudy()
            shareInFunction()
            repeatOnLifecycleFunction()

        }
        viewModel.downloadBySharedFlow()
    }

    /**
     *  通过使用repeatOnLifecycle方法来避免在使用StateFlow或SharedFlow的时候引起的内存泄漏问题。
     *  repeatOnLifecycle是通过观察指定组件的声明周期方法来避免内存泄漏。
     */
    private suspend fun repeatOnLifecycleFunction() {
        repeatOnLifecycle(Lifecycle.State.STARTED) {
            viewModel.numberStateFlow.collect {
                Log.d(
                    TAG,
                    "onCreate: wyj element:$it, replayCache:${viewModel.numberStateFlow.replayCache}"
                )
            }
        }
    }

    /**
     *  Flow通过扩展函数shareIn转化为SharedFlow
     */
    private suspend fun CoroutineScope.shareInFunction() {
        val sharedFlow =
            mutableListOf(0, 1, 2, 3).asFlow().shareIn(this, SharingStarted.Eagerly, 3)
        sharedFlow.collect {
            Log.d(TAG, "onCreate: wyj element:$it, replayCache:${sharedFlow.replayCache}")
        }
    }

    /**
     *  当有数据变化的时候，SharedFlow会把新的数据存到buffer中。当有新的订阅者时，优先从buffer中获取值，
     *  获取的replayCache只是指定replay大小的片段
     */
    private suspend fun sharedFlowReplayCacheStudy() {
        var index = 0
        viewModel.numberStateFlow.collect {
            Log.d(
                TAG,
                "onCreate: wyj 第${index++}次变化，element:$it, replayCache:${viewModel.numberStateFlow.replayCache}"
            )
        }
    }

    private fun CoroutineScope.sharedFlowImpReplayStudy() {
        launch {
            viewModel.numberStateFlow.collect {
                Log.d(TAG, "onCreate: wyj shared first launch element:$it")
            }
        }

        launch {
            delay(3000L)
            viewModel.numberStateFlow.collect {
                Log.d(TAG, "onCreate: wyj shared second launch element:$it")
            }
        }
    }

}