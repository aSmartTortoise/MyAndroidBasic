package com.jie.livedata

import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.arch.core.util.Function
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.Transformations
import kotlin.Function as Function1001

/**
 *  http://liuwangshu.cn/application/jetpack/4-livedata-use.html
 *  LiveData是一个可被观察的数据持有者。LiveData可以关联LifecycleOwner和Observer。
 *  当数据发生变化，且LifecycleOwner处于active的时候，observer会被通知到。
 *  当LifecyclerOwner处于Lifecycler.State.STARTED或者是Lifecycle.State.RESUMED的时候
 *  即为active。当LiveData通过observe关联LifecycleOwner和Observer的时候，这样Observer就订阅了
 *  LiveData，当LifecycleOwner的状态处于Lifecycle.State.DESTROYED的时候，不需要手动unsubscribe
 *  取消订阅这种观察被观察关系。
 *  MutableLiveData是LiveData的实现类。有postValue和setValue方法，setValue方法只能在主线程调用。
 *  postValue方法没有限制。
 *  如果想在LiveData对象分发给观察者之前，修改其中的数据，可以使用Transformations.map()方法和
 *  Transformations.switchMap().
 *  
 */
class MainActivity : AppCompatActivity() {
    companion object {
        const val TAG = "MainActivity"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val liveData: MutableLiveData<String> = MutableLiveData()
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

        liveData.postValue("Android Jetpack architecture component: LiveData")

    }
}