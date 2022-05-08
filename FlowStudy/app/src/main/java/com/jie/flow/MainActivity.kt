package com.jie.flow

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.consumeEach
import kotlinx.coroutines.channels.produce
import kotlinx.coroutines.flow.*
import java.lang.NullPointerException
import java.text.BreakIterator
import kotlin.system.measureTimeMillis

/**
 *  flow学习
 *  1 https://juejin.cn/post/7034381227025465375/#heading-4
 *  2 https://juejin.cn/post/7046155761948295175/
 *  Flow每次收集完后就会销毁。后续也不能发射新的值到流中。
 *  2.1 Channel通道
 *      Channel是个热数据流。
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

        findViewById<View>(R.id.btn_flow_buffer).setOnClickListener {
            bufferStudy()
        }

        findViewById<View>(R.id.btn_channel01).setOnClickListener {
//            channelStudy01()
//            channelStudy02()
            channelStudy03()
        }

        findViewById<View>(R.id.btn_produce).setOnClickListener {
            produceStudy()
        }

        findViewById<View>(R.id.btn_hot_flow).setOnClickListener {
            Intent(this@MainActivity, HotFlowStudyActivity::class.java).apply {
                this@MainActivity.startActivity(this)
            }
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
//            zipOperatorStudy()
//            takeOperatorStudy()
//            takeWhileOperatorStudy()
            dropOperatorStudy()
        }
    }

    /**
     *  drop操作符原始流中忽略前count个元素，剩下的元素组成新的流返回
     */
    private suspend fun dropOperatorStudy() {
        flowOf(0, 1, 2, 3, 5).drop(3).collect { Log.d(TAG, "dropOperatorStudy: wyj value:$it") }
    }

    /**
     * 从流中的第一个元素开始根据指定的条件检查，如果元素不符合指定的条件则取消
     */
    private suspend fun takeWhileOperatorStudy() {
        flowOf(1, 2, 3, 2, 1, 4, 2).takeWhile { it <= 3 }.onCompletion { cause ->
            Log.d(TAG, "takeWhileOperatorStudy: wy cause:$cause")
        }.collect {
            Log.d(TAG, "operatorStudy: wyj value:$it")
        }
    }

    /**
     *  take操作符，返回原始流中第一批count个元素组成的流
     */
    private suspend fun takeOperatorStudy() {
        (1..3).asFlow().take(2).collect { Log.d(TAG, "takeOperatorStudy: wyj it:$it") }
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

    private fun bufferStudy() {
        lifecycleScope.launch {
//            measureFlowTime()
            measureFlowBufferTime()
        }
    }

    /**
     *  花费的时间为等待发射第一个数字等待的100MS+ 搜集每个元素等待的300MS
     */
    private suspend fun measureFlowBufferTime() {
        val time = measureTimeMillis {
            flow<Int> {
                for (i in 1..3) {
                    delay(100L)
                    emit(i)
                }
            }.buffer().collect { value ->
                delay(300L)
                Log.d(TAG, "measureFlowBufferTime: wyj value:$value")
            }
        }
        Log.d(TAG, "measureFlowBufferTime: wyj time:$time")
    }

    /**
     *  花费的时间为等待发射每个数字等待的100MS+ 搜集每个元素等待的300MS
     */
    private suspend fun measureFlowTime() {
        val time = measureTimeMillis {
            flow<Int> {
                for (i in 1..3) {
                    delay(100L)
                    emit(i)
                }
            }.collect { value ->
                delay(300L)
                Log.d(TAG, "measureFlowTime: wyj value:$value")
            }
        }
        Log.d(TAG, "measureFlowTime: wyj time:$time")
    }

    /**
     *  channel提供了一种在流中传输值的方法。我们在使用Channel的时候，发送和接收运行在不同的协程中。
     */
    private fun channelStudy01() {
        lifecycleScope.launch {
            val channel = Channel<Int>()
            launch {
                for (x in 1..5) channel.send(x)
            }
            launch {
                delay(1000L)
                channel.send(666)
                channel.send(999)
            }

            repeat(Int.MAX_VALUE) {
                Log.d(TAG, "channelStudy01: wyj receive:${channel.receive()}")
            }

            Log.d(TAG, "channelStudy01: wyj done")
        }
    }

    /**
     *  Channel关闭之后，继续调用receive方法，导致协程异常结束。
     */
    private fun channelStudy02() {
        lifecycleScope.launch {
            val channel = Channel<Int>()
            launch {
                for (x in 1..5) channel.send(x)
                channel.close()
            }
            launch {
                delay(1000L)
                channel.send(666)
                channel.send(999)
            }

            repeat(Int.MAX_VALUE) {
                Log.d(TAG, "channelStudy02: wyj receive:${channel.receive()}")
            }

            Log.d(TAG, "channelStudy02: wyj done")
        }
    }

    /**
     *  通过判断isClosedForSend 来控制是否send和receive来规避异常。
     */
    @ExperimentalCoroutinesApi
    private fun channelStudy03() {
        lifecycleScope.launch {
            val channel = Channel<Int>()
            launch {
                if (!channel.isClosedForSend) {
                    for (x in 1..5) channel.send(x)
                    channel.close()
                }
            }
            launch {
                delay(1000L)
                if (!channel.isClosedForSend) {
                    channel.send(666)
                    channel.send(999)
                }
            }

            while(true) {
                if (!channel.isClosedForSend) {
                    Log.d(TAG, "channelStudy03: wyj receive:${channel.receive()}")
                } else {
                    break
                }
            }

            Log.d(TAG, "channelStudy03: wyj done")
        }
    }

    @ExperimentalCoroutinesApi
    private fun produceStudy() {
        lifecycleScope.launch {
            val square = produce {
                for (x in 1..5) send(x)
            }
            square.consumeEach { Log.d(TAG, "produceStudy: wyj it:$it") }
            Log.d(TAG, "produceStudy: wyj done")
        }
    }

    override fun onDestroy() {
        lifecycleScope.cancel()
        super.onDestroy()
    }
}