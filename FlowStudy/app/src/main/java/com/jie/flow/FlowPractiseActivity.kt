package com.jie.flow

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.lifecycle.lifecycleScope
import com.jie.flow.practise.CountDown
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

/**
 *  https://juejin.cn/post/6989032238079803429#heading-2
 */
class FlowPractiseActivity : AppCompatActivity() {
    companion object {
        const val TAG = "FlowPractiseActivity"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_flow_practise)
        findViewById<View>(R.id.btn_traditional).setOnClickListener {
            CountDown(10_000, 2_000) { remainTime ->
                100
            }.apply {
                onStart = {
                    Log.d(TAG, "wyj onStart")
                }
                onEnd = {
                    Log.d(TAG, "wyj onEnd result:$it")
                }
                accumulator = { acc, value ->
                    acc + value
                }
            }.start()
        }

        findViewById<View>(R.id.btn_flow).setOnClickListener {
            lifecycleScope.launch {
                val result = countDown(10_000, 2_000) {100}
                    .onStart { Log.d(TAG, "flow start") }
                    .onEach { Log.d(TAG, "wyj each it:$it") }
                    .onCompletion { Log.d(TAG, "wyj flow onCompletion") }
                    .reduce { accumulator, value -> accumulator + value }
                Log.d(TAG, "onCreate: wyj flow result:$result")
            }
        }
    }

    private fun <T> countDown(
        duration: Long,
        interval: Long,
        onCountdown: suspend (Long) -> T
    ): Flow<T> = flow {
        (duration - interval downTo 0 step interval).forEach {
            emit(it)
        }
    }.onEach {
        delay(interval)
    }
        .map { onCountdown(it) }
        .flowOn(Dispatchers.Default)
}