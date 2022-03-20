package com.jie.flow

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import java.lang.NullPointerException
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

        findViewById<View>(R.id.btn_flow_operator).setOnClickListener {
            operatorStudy()
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
                Log.d(
                    TAG,
                    "flowOnStudy: wyj collect context:${currentCoroutineContext()}, value:$value"
                )
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
            Log.d(
                TAG,
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

    private fun operatorStudy() {
        lifecycleScope.launch {
//            startEachCompletionOperator()
//            flowExceptionStudy()
//            flowCatchStudy()
//            transformOperatorStudy()
//            mapOperatorStudy()
//            filterOperatorStudy()
            zipOperatorStudy()
        }
    }

    private suspend fun zipOperatorStudy() {
        val flow1 = (0..3).asFlow()
        val flow2 = flowOf("zero", "one", "two")
        flow1.zip(flow2) { value1, value2 ->
            "[$value1:$value2]"
        }.collect { Log.d(TAG, "operatorStudy: wyj value:$it") }
    }

    /**
     *  filterNot与filter相反，它取得是不满足条件的值
     */
    private suspend fun filterOperatorStudy() {
        (1..5).asFlow().filter {
            it % 2 == 0
        }.collect {
            Log.d(TAG, "filterOperatorStudy: wyj it:$it")
        }
    }

    private suspend fun mapOperatorStudy() {
        flow<Int> {
            emit(1)
        }.map {
            Log.d(TAG, "operatorStudy: map 第一次map转换")
            it * 5
        }.map {
            Log.d(TAG, "operatorStudy: wyj 第一次map转换后的值$it")
            Log.d(TAG, "operatorStudy: wyj 第二次map转换")
            "map $it"
        }.collect {
            Log.d(TAG, "operatorStudy: wyj 最后的值$it")
        }
    }

    private suspend fun transformOperatorStudy() {
        (1..3).asFlow().transform { value ->
            emit("transform $value")
        }.collect { value -> Log.d(TAG, "operatorStudy: wyj collect value:$value") }
    }

    /**
     *  catch操作符只能捕获上游流的异常，onCompletion中没有捕获到异常。
     *  如果该操作符下游的流出现异常时捕获不到的。但是如果异常出现在onCollect末端流操作符中，那么只能使用
     *  try/catch获取协程上下文的CoroutineExceptionHandler进行处理，否则会crash。
     */
    private suspend fun flowCatchStudy() {
        flow<Int> {
            Log.d(TAG, "flowCatchStudy: wyj flow emit")
            emit(1)
        }.onStart {
            Log.d(TAG, "flowCatchStudy: wyj onStart")
        }.onEach {
            Log.d(TAG, "flowCatchStudy: wyj onEach")
            throw NullPointerException("空指针")
        }.catch { cause ->
            Log.d(TAG, "flowCatchStudy: wyj cause:$cause")
            emit(2)
        }.map {
            it * 2
            throw NullPointerException("新的空指针")
        }.onCompletion { cause ->
            Log.d(TAG, "flowCatchStudy: wyj onCompletion cause:$cause")
        }.collect { value -> Log.d(TAG, "flowCatchStudy: wyj collect value:$value") }
    }

    private suspend fun flowExceptionStudy() {
        flow<Int> {
            Log.d(TAG, "flowExceptionStudy: wyj flow emit")
            emit(1)
        }.onStart {
            Log.d(TAG, "flowExceptionStudy: wyj onStart")
        }.onEach {
            Log.d(TAG, "flowExceptionStudy: wyj onEach")
            throw NullPointerException("空指针")
        }.onCompletion { cause ->
            Log.d(TAG, "flowExceptionStudy: wyj onCompletion cause:$cause")
        }.collect { value -> Log.d(TAG, "flowExceptionStudy: wyj collect value:$value") }
    }

    /**
     *  onStart、onEach、onCompletion操作符，
     *  执行顺序是onStart、flow emit、onEach、collect、onCompletion
     */
    private suspend fun startEachCompletionOperator() {
        flow<Int> {
            Log.d(TAG, "startEachCompletionOperator: wyj flow emit")
            emit(1)
        }.onStart {
            Log.d(TAG, "startEachCompletionOperator: wyj start")
        }.onEach {
            Log.d(TAG, "startEachCompletionOperator: wyj each")
        }.onCompletion {
            Log.d(TAG, "startEachCompletionOperator: wyj completion")
        }.collect { value -> Log.d(TAG, "startEachCompletionOperator: wyj collect value:$value") }
    }

    override fun onDestroy() {
        lifecycleScope.cancel()
        super.onDestroy()
    }
}