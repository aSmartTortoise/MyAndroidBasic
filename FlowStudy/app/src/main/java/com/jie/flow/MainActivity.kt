package com.jie.flow

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.View
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.consumeEach
import kotlinx.coroutines.channels.produce
import kotlinx.coroutines.flow.*
import java.lang.NullPointerException
import kotlin.system.measureTimeMillis

/**
 *  flow学习
 *  1 https://juejin.cn/post/7034381227025465375/
 *      1.1 RestrictsSuspension注解
 *          被RestrictsSuspension注解的类在用作挂起的扩展函数的接收器时有限制，在扩展函数内部只能使用该接收器类定义的挂起函数，而不能
 *      调用任意的挂起函数。
 *      1.2 Flow
 *          Flow是一个异步数据流，它按照顺序发出值。通常说的Flow是指的冷流，即流可以重复收集，并在每次收集的时候触发相同的代码。而SharedFlow
 *      指的是热流，
 *          1.2.1 取消Flow
 *              Flow的执行是依赖于collect的，而collect需要在协程中调用，取消Flow的执行可以通过取消它所在的协程完成。
 *          1.2.2 为了保证Flow上下文的一致性，禁止在Flow的上游流emit代码中切换线程。但是在下游流的collect操作符函数中是可以切换线程的。
 *          1.2.3 Flow中线程的切换
 *              通过Flow的扩展函数flowOn(CoroutineContext)，设置流的执行上下文，该操作符只作用于前面没有相同上下文的操作符或构建器；
 *          该操作符中的上下文不会泄漏到下游流中。不管flowOn如何的切换线程，collect始终运行在调用它的协程的调度器线程上。
 *      1.3 Flow的操作符
 *          以下对Flow操作符的分类不来自于官方。
 *          1.3.1 过渡（流）操作符
 *              过渡操作符用来区分流执行到某一阶段。比如onStart/onEach/onCompletion。
 *              onStart作用于上游流，在上游流执行之前onStart操作先调用。因为入参是挂载的FlowCollector接收器的扩展函数，所以可以
 *          在onStart操作中发送数据。当onStart应用在SharedFlow时，不能保证onStart操作符中的操作发送的只或者上游流发送的值都能被收集。
 *              onEach下游流中每个值收集之前调用onEach的操作。
 *              onCompletion在流完成或者取消的时候调用onCompletion操作，可以报告上游和下游发生的异常，也可以观察到取消的异常，当流完成的
 *          时候异常为空。如果通过取消流所在的协程的方式取消流的执行，且Job#cancel的扩展方法中没有指定CancellationException，则流的
 *          onCompletion会观察到默认的JobCancellationException。
 *          1.3.2 异常操作符
 *              catch操作符可以捕获它的上游流中发生的异常。如果它的下游流中出现了未捕获的异常，则程序会crash；可以继续添加一个catch操作符
 *          来捕获，或者通过try-catch捕获整个流的异常，或者通过协程上下文的CoroutineExceptionHandler来处理。
 *          1.3.3 转换操作符
 *              transform操作符，该操作符的操作可以拿到流中的值并对其进行转换，该操作是一个以FlowCollect为接收器的挂载函数。操作中需要调用
 *          emit函数。
 *              map操作符，操作符的操作可以获取流中的值并对其进行转换，得到一个转换之后的流。
 *              mapNotNull操作符，过滤掉上游流中为null的值，并对其他值进行转换，得到转换后的流。
 *              filter操作符，接受上游流中的值，返回满足给定谓词函数的值构成的流。
 *              filterNot操作符，接受上游流的值，返回不满足指定谓词函数的值构成的流。
 *              filterIsInstance操作符，接受上游流的值，返回指定数据类型的值构成的流。
 *              filterNotNull操作符，接受上游流的值，返回非null值构成的流。
 *              zip操作符，使用指定的转换函数，将当前流与指定的流进行合并，每一对value应用转换函数，返回新的流。当其中的一个流
 *              完成并且剩余流调用了取消之后新的流立即完成。
 *
 *
 *
 *
 *
 *
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

        findViewById<View>(R.id.btn_state_flow).setOnClickListener {
            Intent(this@MainActivity, StateFlowStudyActivity::class.java).apply {
                this@MainActivity.startActivity(this)
            }
        }

        findViewById<View>(R.id.btn_share_flow).setOnClickListener {
            Intent(this@MainActivity, SharedFlowActivity::class.java).apply {
                this@MainActivity.startActivity(this)
            }
        }


    }

    private fun test() {
//        simple().forEach { value -> Log.d(TAG, "test: wyj value:$value") }
//        cancelFlow()
        lifecycleScope.launch {
//            flowBuildStudy()
//            asFlowBuildStudy()
//            flowOfBuildStudy()
//            flowBuildStudy02()
//            flowBuilderSwitchContext()
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
     *  为了保证flow上下文的一致性，在flow builder中不能切换线程。
     */
    private suspend fun flowBuilderSwitchContext() {
        flow {
            for (element in 1..3) {
                delay(100L)
                // 为了保证Flow上下文的一致性，在Flow的上游流中切换线程会抛出异常
                // java.lang.IllegalStateException: Flow invariant is violated:
                if (element == 2) {
                    withContext(Dispatchers.IO) {
                        emit(element)
                    }
                } else {
                    emit(element)
                }
            }
        }.collect { value -> Log.d(TAG, "flowBuilderSwitchContext: wyj value:$value") }

        (1..3).asFlow().collect { value ->
            if (value == 2) {
                withContext(Dispatchers.IO) {
                    Log.d(TAG, "flowBuilderSwitchContext: wyj value:$value")
                }
            } else {
                Log.d(TAG, "flowBuilderSwitchContext: wyj value:$value")
            }
        }

        flow {
            for (element in 1..3) {
                delay(100L)
                emit(element)
            }
        }.collect { value ->
            if (value == 2) {
                withContext(Dispatchers.IO) {
                    Log.d(TAG, "flowBuilderSwitchContext: wyj value:$value")
                }
            } else {
                Log.d(TAG, "flowBuilderSwitchContext: wyj value:$value")
            }
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
     *  通过取消协程，来取消Flow的执行。
     */
    private fun cancelFlow() {
        val job = lifecycleScope.launch {
            flow<Int> {
                for (element in 1..3) {
                    delay(100L)
                    emit(element)
                }
            }.collect { value -> Log.d(TAG, "cancelFlow: wyj value:$value") }
        }
        Handler().postDelayed({ job.cancel() }, 200L)
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
        /**
         *  通过取消流所在的协程的方式取消流的执行，onCompletion操作中可以观察到取消异常。
         */
//        val job = lifecycleScope.launch {
//            flow {
//                for (element in 1..3) {
//                    delay(100L)
//                    Log.d(TAG, "operatorStudy: wyj emit element:${element}")
//                    emit(element)
//                }
//            }.onCompletion { cause ->
//                Log.d(TAG, "operatorStudy: wyj onCompletion cause:$cause")
//            }.collect { value ->
//                Log.d(TAG, "operatorStudy: wyj collect value:$value")
//            }
//        }
//        Handler().postDelayed(
//            {
//                job.cancel()
//            }, 200L
//        )
        lifecycleScope.launch {
//            startEachCompletionOperator()
//            flowExceptionStudy()
//            catchOperatorStudy()
//            transformOperatorStudy()
//            mapOperatorStudy()
//            mapNotNullOperator()
//            filterOperatorStudy()
//            filterNotOperator()
//            filterIsInstanceOperator()
//            filterNotNullOperator()
            zipOperatorStudy()
//            takeOperatorStudy()
//            takeWhileOperatorStudy()
//            dropOperatorStudy()
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
        val flow1 = (0..4).asFlow()
        val flow2 = flowOf("zero", "one", "two")
        flow1.zip(flow2) { value1, value2 ->
            "[$value1:$value2]"
        }.collect { Log.d(TAG, "operatorStudy: wyj value:$it") }
    }

    private suspend fun filterNotNullOperator() {
        flowOf(0, null, 1, 2)
            .filterNotNull()
            .collect { value -> Log.d(TAG, "filterNotNull: wyj value:$value") }
    }

    private suspend fun filterIsInstanceOperator() {
        flowOf(0, null, 1, 2)
            .filterIsInstance<Int>()
            .collect { value ->
                Log.d(TAG, "filterInstanceOperator: wyj value:$value")
            }
    }

    private suspend fun filterNotOperator() {
        (1 .. 5).asFlow()
            .filterNot {
                it % 2 == 0
            }.collect { value ->
                Log.d(TAG, "filterNotOperator: wyj value:$value")
            }
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

    private suspend fun mapNotNullOperator() {
        flowOf("one", "two", null, "four").mapNotNull {
            it
        }.collect { value -> Log.d(TAG, "mapNotNull: wyj value:$value") }
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
            Log.d(TAG, "operatorStudy: wyj collect 最后的值:$it")
        }
    }

    private suspend fun transformOperatorStudy() {
        (1..3).asFlow().transform { value ->
            emit("transform $value")
        }.collect { value -> Log.d(TAG, "operatorStudy: wyj collect value:$value") }
    }

    /**
     *  catch操作符只能捕获上游流的异常。
     *  如果该操作符下游的流出现未捕获的异常，则会crash。但是如果异常出现在onCollect末端流操作符中，那么只能使用
     *  try/catch获取协程上下文的CoroutineExceptionHandler进行处理，否则会crash。
     */
    private suspend fun catchOperatorStudy() {
        flow<Int> {
            Log.d(TAG, "flowCatchStudy: wyj flow emit")
            emit(1)
        }.onStart {
            Log.d(TAG, "flowCatchStudy: wyj onStart")
        }.onEach {
            Log.d(TAG, "flowCatchStudy: wyj onEach")
            throw NullPointerException("空指针")
        }.catch { cause ->
            Log.d(TAG, "flowCatchStudy: wyj catch cause:$cause")
            emit(2)
        }.map { value ->
            value * 2
            throw IllegalStateException("map operator throw exception.")
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
            for (element in 1..3) {
                emit(element)
            }
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

            while (true) {
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