package com.jie.flow

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import kotlin.coroutines.coroutineContext

/**
 *  flow学习
 *  https://juejin.cn/post/7034381227025465375/#heading-4
 */
class MainActivity : AppCompatActivity() {
    companion object {
        const val TAG = "MainActivity"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        findViewById<View>(R.id.btn_sequence).setOnClickListener {
            test()
        }

    }

    private fun test() {
//        simple().forEach { value -> Log.d(TAG, "test: wyj value:$value") }

        lifecycleScope.launch {
//            flowBuildStudy()
//            asFlowBuildStudy()
//            flowOfBuildStudy()
//            flowBuildStudy02()
            flowOnStudy()

        }
    }

    /**
     *  flowOn可以将流的上下文改成指定的上下文。
     *  flowOn可以进行组合使用。
     *  flowOn只影响前面没有上下文的操作符。已经有上下文的操作符不会受到后面flowOn的影响。
     *  无论flowOn如何切换线程，collect代码块始终运行在协程调度器所关联的线程上。
     */
    private suspend fun flowOnStudy() {
        flow<Int> {
            for (i in 1..3) {
                Log.d(TAG, "flowOnStudy: wyj flow context:${currentCoroutineContext()}")
                delay(100L)
                emit(i)
            }
        }.flowOn(Dispatchers.IO)
            .map {
                Log.d(TAG, "flowOnStudy: wyj map context:${currentCoroutineContext()}")
                it * 2
            }
            .flowOn(Dispatchers.Default)
            .collect { value ->
            Log.d(TAG,
                "flowOnStudy: wyj collect context:${currentCoroutineContext()}, value:$value")
        }
    }

    /**
     *  lifecycleScope launch的协程是在主线程上执行的。
     */
    private suspend fun flowBuildStudy02() {
        flow<Int> {
            for (i in 1..3) {
                Log.d(TAG, "flowBuildStudy02: wyj context:${currentCoroutineContext()}")
                delay(100L)
                emit(i)
            }
        }.collect { value ->
            Log.d(TAG,
                "flowBuildStudy02: wyj context:${currentCoroutineContext()} value:$value"
            )
        }
    }

    /**
     *  flowOf构建器函数接受单个参数或者可变参数，其实质也是调用flow构建器函数
     */
    private suspend fun flowOfBuildStudy() {
        flowOf(1, 2, 3, 4).collect { value -> Log.d(TAG, "flowOfBuildStudy: wyj value:$value") }
    }

    /**
     *  asFlow构建器构建冷数据流Flow，其实质也是通过flow构建器函数实现的
     */
    private suspend fun asFlowBuildStudy() {
        (1..3).asFlow().collect { value -> Log.d(TAG, "asFlowBuildStudy: wyj value:$value") }
    }

    /**
     *  通过扩展函数flow构建一个冷数据流Flow，通过emit函数来发射数据，通过collect函数来收集这些
     *  数据，因为collect函数是挂起函数，所以需要在协程中操作。
     *  Flow没有提供取消的操作，Flow的取消可以依赖协程的取消，
     */
    private suspend fun flowBuildStudy() {
        flow<Int> {
            for (i in 1..3) {
                delay(100L)
                emit(i)
            }
        }.collect { value -> Log.d(TAG, "flowBuildStudy: wyj value:$value") }
    }

    private fun simple(): Sequence<Int> = sequence {
        for (i in 1..3) {
            Thread.sleep(100L)
            yield(i)
        }
    }

    override fun onDestroy() {
        lifecycleScope.cancel()
        super.onDestroy()
    }
}