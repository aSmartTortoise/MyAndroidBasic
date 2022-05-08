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
import java.lang.RuntimeException

class HotFlowStudyActivity : AppCompatActivity() {
    companion object {
        const val TAG = "HotFlowStudyActivity"
    }
    private val viewModel: TestFlowViewModel by viewModels()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_hot_flow_study)
        lifecycleScope.launch {
            //当StateFlow的状态为某一个临界值的时候，终止对这个数据流数据收集，执行下一个StateFlow数据流
            //的收集
            try {
                viewModel.mState.collect {
                    Log.d(TAG, "onCreate: wyj state:$it")
                    if (it == 3) {
                        throw RuntimeException("终止第一个StateFlow 数据流的收集")
                    }
                }
            } catch (e: Exception) {
                Log.d(TAG, "onCreate: wyj e:$e")
            }

            viewModel.mName.collect {
                Log.d(TAG, "onCreate: wyj name:$it")
            }
        }
        viewModel.download()
    }

    override fun onDestroy() {
        lifecycleScope.cancel()
        super.onDestroy()
    }
}