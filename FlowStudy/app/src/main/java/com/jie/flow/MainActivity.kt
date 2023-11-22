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
            channelStudy01()
//            closeChannelBeforeSend()
            handleCloseExceptionBeforeReceive()
        }

        findViewById<View>(R.id.btn_produce).setOnClickListener {
            produceStudy()
        }

        findViewById<View>(R.id.btn_state_flow01).setOnClickListener {
            stateFlowStudy()
        }

        findViewById<View>(R.id.btn_shared_flow01).setOnClickListener {
            sharedFlowStudy()
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

        findViewById<View>(R.id.btn_flow_practise).setOnClickListener {
            Intent(this@MainActivity, FlowPractiseActivity::class.java).apply {
                this@MainActivity.startActivity(this)
            }
        }


    }

    private fun sharedFlowStudy() {
        mutableSharedFlowEmit()
//        mutableSharedFlowTryEmit()
//        mutableSharedFlowEmitSuspend()
//        shareInEagerly()
//        shareInLazily()
//        shareInWhileSubscribed()
    }

    private fun shareInWhileSubscribed() {
        lifecycleScope.launch {
            var start = 0L
            val sharedFlow = (1..10).asFlow()
                .onStart { start = currentTime() }
                .onEach {
                    Log.d(TAG, "shareInWhileSubscribed: wyj each it:$it, ${currentTime() - start}")
                    delay(100L)
                }
                .onCompletion {
                    Log.d(
                        TAG,
                        "shareInWhileSubscribed: wyj onCompletion ${currentTime() - start}"
                    )
                }
                .shareIn(
                    this,
                    SharingStarted.WhileSubscribed(100L, 200L),
                    replay = 2
                )
            val job = launch {
                Log.d(TAG, "shareInWhileSubscribed: wyj current time")
                sharedFlow.collect {
                    Log.d(
                        TAG,
                        "shareInWhileSubscribed: wyj collect it:$it, ${currentTime() - start}"
                    )
                }
            }
            launch {
                delay(1000L)
                job.cancel()
                delay(110L)
                sharedFlow.collect {
                    Log.d(
                        TAG,
                        "shareInWhileSubscribed: wyj again collect it:$it, ${currentTime() - start}"
                    )
                }
                Log.d(TAG, "shareInWhileSubscribed: wyj sharedFlow has stop")
            }
        }
    }

    private fun currentTime() = System.currentTimeMillis()

    private fun shareInLazily() {
        lifecycleScope.launch {
            val sharedFlow = flowOf(1, 2, 3, 4)
                .onEach { Log.d(TAG, "shareInLazily: wyj each it:$it") }
                .shareIn(this, SharingStarted.Lazily, replay = 2)
            launch {
                sharedFlow.collect { value1 ->
                    Log.d(
                        TAG,
                        "shareInLazily: wyj collect value1:$value1"
                    )
                }
            }

            delay(10L)
            launch {
                sharedFlow.collect { value2 ->
                    Log.d(
                        TAG,
                        "shareInLazily: wyj collect value2:$value2"
                    )
                }
            }
        }

    }

    private fun shareInEagerly() {
        lifecycleScope.launch {
            val sharedFlow = flowOf(1, 2, 3, 4)
                .onEach { Log.d(TAG, "shareInOperator: wyj each it:$it") }
                .shareIn(this, SharingStarted.Eagerly, replay = 2).onCompletion {
                    Log.d(TAG, "shareInOperator: wyj onCompletion")
                }
            launch {
                sharedFlow.collect { value ->
                    Log.d(
                        TAG,
                        "shareInOperator: wyj collect value:$value"
                    )
                }
            }
        }
    }

    private fun mutableSharedFlowEmitSuspend() {
        val shareFlow = MutableSharedFlow<Int>(4, 3)
        lifecycleScope.launch {
            launch {
                shareFlow.collect { value ->
                    Log.d(
                        TAG,
                        "mutableSharedFlowEmitSuspend: wyj collect value:$value"
                    )
                }
            }
            launch {
                (1..10).forEach {
                    shareFlow.emit(it)
                    Log.d(TAG, "mutableSharedFlowEmitSuspend: wyj emit it:$it")
                }
            }
            delay(200)
            val list = shareFlow.replayCache
            Log.d(TAG, "mutableSharedFlowEmitSuspend: wyj list:$list")
            launch {
                shareFlow.collect { value2 ->
                    Log.d(
                        TAG,
                        "mutableSharedFlowEmitSuspend: wyj collect value2:$value2"
                    )
                }
            }
        }
    }

    private fun mutableSharedFlowTryEmit() {
        val shareFlow = MutableSharedFlow<Int>(4, 3)
        lifecycleScope.launch {
            launch {
                shareFlow.collect { value ->
                    Log.d(
                        TAG,
                        "mutableSharedFlowTryEmit: wyj collect value:$value"
                    )
                }
            }
            launch {
                (1..10).forEach {
                    val result = shareFlow.tryEmit(it)
                    Log.d(TAG, "mutableSharedFlowTryEmit: wyj emit it:$it, result:$result")
                    delay(100L)
                }
                Log.d(TAG, "mutableSharedFlowTryEmit: wyj not suspend.")
            }
            delay(900L)
            launch {
                shareFlow.collect { value2 ->
                    Log.d(
                        TAG,
                        "mutableSharedFlowTryEmit: wyj collect value2:$value2"
                    )
                }
            }
        }
    }

    private fun mutableSharedFlowEmit() {
        val shareFlow = MutableSharedFlow<Int>(4, 3)
        lifecycleScope.launch {
            launch {
                shareFlow.collect {
                    Log.d(TAG, "mutableSharedFlowEmit: wyj collect it:$it")
                }
            }
            val emitJob = launch {
                (1..10).forEach {
                    Log.d(TAG, "mutableSharedFlowEmit: wyj emit it:$it")
                    shareFlow.emit(it)
                    delay(100L)
                }
                Log.d(TAG, "mutableSharedFlowEmit: wyj emitJob not suspend")
            }

            delay(900)
            launch {
                shareFlow.collect { value2 -> Log.d(TAG, "sharedFlowStudy: wyj value2:$value2") }
            }
        }
    }

    private fun stateFlowStudy() {
//        stateFlowCollector()
//        stateFlowCollectValue()
//        stateInOperator()
//        stateFlowNoBuffer()
//        compareAndSet()
    }

    private fun compareAndSet() {
        lifecycleScope.launch {
            val stateFlow = MutableStateFlow(1)
            launch {
                for (value in 1..5) {
                    delay(100L)
                    stateFlow.emit(value)
                }
            }

            launch {
                stateFlow.collect { value ->
                    Log.d(TAG, "compareAndSet: wyj collect value:$value")
                    stateFlow.compareAndSet(3, 300)
                }
            }

            launch {
                stateFlow.collect { value2 ->
                    Log.d(
                        TAG,
                        "compareAndSet: wyj collect value2:$value2"
                    )
                }
            }
        }
    }

    private fun stateFlowNoBuffer() {
        lifecycleScope.launch {
            val stateFlow = flow {
                for (value in 1..5) {
                    Log.d(TAG, "stateFlowNoBuffer: wyj emit value:$value")
                    emit(value)
                }
            }.stateIn(this)

            launch {
                stateFlow.collect { value ->
                    Log.d(TAG, "stateFlowNoBuffer: wyj collect value:$value")
                }
            }
        }
    }

    private fun stateInOperator() {
        lifecycleScope.launch {
            val stateFlow = flow {
                for (element in 0..3) {
                    emit(element)
                    delay(100)
                }
            }.stateIn(this)
            launch {
                stateFlow.collect { value ->
                    Log.d(TAG, "stateInOperator: wyj collect value:$value")
                }
            }
        }
    }

    private fun stateFlowCollectValue() {
        val stateFlow = MutableStateFlow(0)
        lifecycleScope.launch {
            val collectJob = launch {
                stateFlow.collect { value ->
                    Log.d(TAG, "stateFlowCollectValue: wyj collect value:$value")
                }
            }

            launch {
                Log.d(TAG, "stateFlowCollectValue: wyj set value")
                stateFlow.emit(3)
            }

            launch {
                stateFlow.collect { value2 ->
                    Log.d(
                        TAG,
                        "stateFlowCollectValue: wyj collect value2:$value2"
                    )
                }
            }
            delay(200L)
            Log.d(
                TAG,
                "stateFlowCollectValue: wyj collectJob isCompleted:${collectJob.isCompleted}"
            )
        }
    }

    /**
     *  通常StateFlow的收集器执行永不会完成，挂起了调用者，这样所在的协程也不会完成。
     */
    private fun stateFlowCollector() {
        val stateFlow = MutableStateFlow(0)
        lifecycleScope.launch {
            stateFlow.collect { value ->
                Log.d(TAG, "stateFlowCollector: wyj collect value:$value")
            }
            Log.d(TAG, "stateFlowCollector: wyj done!")
        }
    }

    private fun test() {
//        simple().forEach { value -> Log.d(TAG, "test: wyj value:$value") }
//        cancelFlow()
        lifecycleScope.launch {
//            flowBuildStudy()
            flowExecuteAnalyse()
//            asFlowBuildStudy()
//            flowOfBuildStudy()
//            flowBuildStudy02()
//            flowBuilderSwitchContext()
//            flowOnStudy()

        }
    }

    /**
     *  flowOn可以将流的上下文改成指定的上下文。
     *  flowOn可以进行组合使用。
     *  flowOn只影响前面没有上下文的操作符。已经有上下文的操作符不会受到后面flowOn的影响。
     *  无论flowOn如何切换线程，collect代码块始终运行在协程调度器所关联的线程上。
     */
    private suspend fun flowOnStudy() {
        val flow = flow<Int> {
            for (i in 1..3) {
                Log.d(TAG, "flowOnStudy: wyj flow context:${currentCoroutineContext()}")
                delay(100L)
                emit(i)
            }
        }
            .flowOn(Dispatchers.IO)
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

    private suspend fun flowExecuteAnalyse() {
//        flow {
//            for (element in 1..3) {
//                emit(element)
//            }
//        }
//            .map {
//                it + 10
//            }
//            .collect(object : FlowCollector<Int> {
//                override suspend fun emit(value: Int) {
//                    Log.d(TAG, "emit: emit value:$value")
//                }
//
//            })

        val safeFlow = flow {
            for (element in 1..3) {
                Log.d(TAG, "flowExecuteAnalyse: flow operator element:$element")
                emit(element)
            }
        }
        val flowOn = safeFlow.flowOn(Dispatchers.IO)
        val mapFlow = flowOn.map {
            Log.d(TAG, "flowExecuteAnalyse: map operator it:$it")
            it + 10
        }
        val collector = object : FlowCollector<Int> {
            override suspend fun emit(value: Int) {
                Log.d(TAG, "flowExecuteAnalyse collect operator value:$value")
            }

        }
        mapFlow.collect(collector)
    }

    /**
     *  通过顶层函数flow构建一个冷数据流Flow，通过emit函数来发射数据，通过collect函数来收集这些
     *  数据，因为collect函数是挂起函数，所以需要在协程中操作。
     *  Flow没有提供取消的操作，Flow的取消可以依赖协程的取消，
     */
    private suspend fun flowBuildStudy() {
        val flow = flow<Int> {
            for (i in 1..3) {
                delay(100L)
                Log.d(TAG, "flowBuildStudy: wyj emit value:$i")
                emit(i)
            }
        }
        flow.collect { value -> Log.d(TAG, "flowBuildStudy: wyj collect value:$value") }
        flow.collect { value2 -> Log.d(TAG, "flowBuildStudy: wyj collect value2:$value2") }
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
//            zipOperatorStudy()
//            takeOperatorStudy()
//            takeWhileOperatorStudy()
//            dropOperatorStudy()
            toListOperator()
        }
    }

    private suspend fun toListOperator() {
        val list = (1..3).asFlow().toList()
    }

    /**
     *  drop操作符原始流中忽略前count个元素，剩下的元素组成新的流返回
     */
    private suspend fun dropOperatorStudy() {
        flowOf(0, 1, 2, 3, 5)
            .drop(3)
            .onCompletion { cause -> Log.d(TAG, "dropOperatorStudy: wyj cause:$cause") }
            .collect { Log.d(TAG, "dropOperatorStudy: wyj value:$it") }
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
        (1..3).asFlow()
            .take(5)
            .onCompletion { cause -> Log.d(TAG, "takeOperatorStudy: wyj cause:$cause") }
            .collect { Log.d(TAG, "takeOperatorStudy: wyj it:$it") }
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
        (1..5).asFlow()
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
            throw IllegalStateException("map operator throw exception.")
            value * 2
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
            flow {
                for (i in 1..3) {
                    delay(100L)
                    Log.d(
                        TAG,
                        "measureFlowBufferTime: wyj emit value:$i, context:${currentCoroutineContext()}"
                    )
                    emit(i)
                }
            }.onEach {
                Log.d(
                    TAG,
                    "measureFlowBufferTime: wyj each $it context:${currentCoroutineContext()}"
                )
            }.buffer()
                .onCompletion {
                    Log.d(TAG, "measureFlowBufferTime: wyj context:${currentCoroutineContext()}")
                }
                .collect { value ->
                    delay(300L)
                    Log.d(
                        TAG,
                        "measureFlowBufferTime: wyj collect value:$value context:${currentCoroutineContext()}"
                    )
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
    private fun closeChannelBeforeReceive() {
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
    private fun handleCloseExceptionBeforeReceive() {
        lifecycleScope.launch {
            val channel = Channel<Int>()
            launch {
                if (!channel.isClosedForSend) {
                    for (x in 1..5) {
                        Log.d(TAG, "channelStudy03: wyj send x:$x")
                        channel.send(x)
                    }
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
            val receiveChannel = produce {
                for (x in 1..5) {
                    Log.d(TAG, "produceStudy: wyj send x:$x")
                    send(x)
                }
            }
            receiveChannel.consumeEach { Log.d(TAG, "produceStudy: wyj it:$it") }
            Log.d(TAG, "produceStudy: wyj isClosedForReceive:${receiveChannel.isClosedForReceive}")
            if (receiveChannel is CoroutineScope) {
                Log.d(
                    TAG,
                    "produceStudy: isCompleted:${(receiveChannel as CoroutineScope).coroutineContext[Job]?.isCompleted}"
                )
            }
            Log.d(TAG, "produceStudy: wyj done")
        }
    }

    override fun onDestroy() {
        lifecycleScope.cancel()
        super.onDestroy()
    }
}