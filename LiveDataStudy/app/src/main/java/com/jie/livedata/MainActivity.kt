package com.jie.livedata

import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.View
import android.widget.Button
import androidx.annotation.RequiresApi
import androidx.arch.core.util.Function
import androidx.lifecycle.*
import com.jie.livedata.coroutine.CoroutineLiveDataActivity
import com.jie.livedata.global.NotifyLiveDataActivity
import kotlin.Function as Function1001

/**
 *  http://liuwangshu.cn/application/jetpack/4-livedata-use.html
 *
 *  https://developer.android.com/topic/libraries/architecture/livedata?hl=zh-cn
 *
 *  LiveData是一个可被观察的数据持有者。LiveData通过observe方法实现LifecycleOwner对
 *  LiveData的订阅关系。当数据发生变化，且LifecycleOwner处于active的时候，LifecycleOwner会被通知到。
 *  当LifecyclerOwner处于Lifecycler.State.STARTED或者是Lifecycle.State.RESUMED的时候
 *  即为active。当LifecycleOwner的状态处于Lifecycle.State.DESTROYED的时候，不需要手动unsubscribe
 *  取消订阅这种观察被观察关系。
 *  MutableLiveData是LiveData的实现类。有postValue和setValue方法，setValue方法只能在主线程调用。
 *  postValue方法没有限制。
 *  1 更改LiveData中的数据
 *  如果想在LiveData对象分发给观察者之前，修改其中的数据，可以使用Transformations.map()方法和
 *  Transformations.switchMap().
 *  2 合并多个LiveData数据源
 *      MediatorLiveData继承MutableLiveData，MediatorLiveData可以合并多个LiveData数据源，
 *  当其中任意一个LiveData数据源的数据发生变化，都会通知给处于ACTIVE状态的LifecycleOwner。
 *
 *  4 LiveData的onActive和onInactive方法
 *  当LiveData关联的处于ACTIVE的观察者数量由0变为1的时候会回调onActive方法；当LiveData关联的处于ACTIVE
 *  状态的观察者数量由1变为0的时候会回调onInactive方法。
 *
 *  https://juejin.cn/post/6955726530911666190
 *  5 LiveData先设置数据，然后Observer订阅LiveData，Observer可以观察到订阅之前LiveData设置的值，即
 *  Observer和LiveData这种订阅关系间的消息是粘性的。
 *
 *
 *
 *
 */
class MainActivity : AppCompatActivity() {
    companion object {
        const val TAG = "MainActivity"
    }

    lateinit var mLiveData1: MutableLiveData<String>
    lateinit var mLiveData2: MutableLiveData<String>
    lateinit var mLiveData3: MutableLiveData<Boolean>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val liveData: MutableLiveData<String> = MutableLiveData()
        liveData.postValue("Android Jetpack architecture component: LiveData")

        liveData.observe(this, object : Observer<String> {
            override fun onChanged(t: String?) {
                Log.d(TAG, "onChanged: wyj t:$t")
            }

        })

        val mapLiveData = Transformations.map(liveData,
            object : Function<String, Any> {
                override fun apply(input: String?): Any {
                    return "$input and transformation map method"
                }
            })

        mapLiveData.observe(this,
            { t -> Log.d(TAG, "onChanged: wyj mapLiveData t:${t.toString()}") })

        // Transformations.switchMap
        mLiveData1 = MutableLiveData()
        mLiveData2 = MutableLiveData()
        mLiveData3 = MutableLiveData()
        val switchMapLiveData =
            Transformations.switchMap(mLiveData3, object : Function<Boolean, LiveData<String>> {
                override fun apply(input: Boolean?): LiveData<String> {
                    input?.let {
                        if (it) {
                            return mLiveData1
                        } else return mLiveData2
                    } ?: return mLiveData1
                }

            })
        switchMapLiveData.observe(this, object : Observer<String> {
            override fun onChanged(t: String?) {
                Log.d(TAG, "onChanged: wyj t:$t")
            }

        })
        mLiveData3.postValue(false)
        mLiveData1.postValue("Android Jetpack architecture component: LiveData 1")
        mLiveData2.postValue("Android Jetpack architecture component: LiveData 2")

        findViewById<Button>(R.id.btn_mediator).setOnClickListener { v -> mediatorStudy() }
        findViewById<View>(R.id.btn_coutdown).setOnClickListener {
            Intent(this, CountDownActivity::class.java).apply {
                this@MainActivity.startActivity(this)
            }
        }

        findViewById<View>(R.id.btn_global_live_data).setOnClickListener {
            Intent(this, NotifyLiveDataActivity::class.java).apply {
                this@MainActivity.startActivity(this)
            }
        }

        findViewById<View>(R.id.btn_coroutine_live_data).setOnClickListener {
            Intent(this, CoroutineLiveDataActivity::class.java).apply {
                this@MainActivity.startActivity(this)
            }
        }
    }

    private fun mediatorStudy() {
        val liveData1 = MutableLiveData<String>()
        val liveData2 = MutableLiveData<String>()
        val mediatorLiveData = MediatorLiveData<String>()
        mediatorLiveData.addSource(liveData1) {t -> Log.d(TAG, "mediatorStudy: wyj t:$t")}
        mediatorLiveData.addSource(liveData2) {s -> Log.d(TAG, "mediatorStudy: wyj s:$s")}
        mediatorLiveData.observe(this) {result -> Log.d(TAG, "mediatorStudy: wyj result:$result")}
        liveData1.postValue("Mediator: Android Jetpack architecture component:")
        Handler().postDelayed({
                              liveData2.postValue("LiveData")
        }, 200L)
    }
}