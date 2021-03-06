package com.jie.lifecycle

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import androidx.lifecycle.*

/**
 * https://developer.android.com/jetpack/androidx/releases/lifecycle
 *  http://liuwangshu.cn/application/jetpack/2-lifecycle-use.html
 *  http://liuwangshu.cn/application/jetpack/3-lifecycle-theory.html
 *
 *  Lifecycler是Android Jetpack architecheture component，是声明周期感知型组件，对Activity和Fragment
 *  生命周期的变化做出相应。
 *  Lifecycle是个抽象类，内部定义两个枚举来跟踪关联组件的声明周期状态，Event、State。
 *  Event对应的事件有：ON_CREATE、ON_START、on_RESUME、ON_PAUSE、ON_STOP、ON_DESTROY、ON_ANY。
 *  前面六个事件都会映射到ACTIVITY的响应的回调事件。
 *  State对应的状态有：INITIALIZED、CREATED、STARTED、RESUMED、DESTROYED。
 *  Event中的事件和State中的转态有映射关系，
 *  ON_CREATE和ON_STOP对应CREATED、ON_START和ON_PAUSE对应STARTED，ON_RESUME对应RESUMED，ON_DESTROY对应
 *  DESTROYED
 *  Activity、Fragment都实现了接口LifecyclerOwner，而LifeCyclerOwner可以认为是一个被观察者。
 *
 */
class MainActivity : AppCompatActivity() {
    companion object {
        const val TAG = "MainActivity"
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        Log.d(TAG, "onCreate: wyj state:${lifecycle.currentState}")
        lifecycle.addObserver(MyLifecyclerObserver())
    }

    override fun onStart() {
        super.onStart()
        Log.d(TAG, "onStart: wyj state:${lifecycle.currentState}")
    }

    override fun onResume() {
        super.onResume()
        Log.d(TAG, "onResume: wyj state:${lifecycle.currentState}")
    }

    override fun onPause() {
        super.onPause()
        Log.d(TAG, "onPause: wyj state:${lifecycle.currentState}")
    }

    override fun onStop() {
        super.onStop()
        Log.d(TAG, "onStop: wyj state:${lifecycle.currentState}")
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d(TAG, "onDestroy: wyj state:${lifecycle.currentState}")
    }

    /**
     *  观察到Activity声明周期状态变化关联的Lifecycle的Event事件。
     *  其中 ON_CREATE、ON_START、ON_RESUME等事件在对应的回调方法执行之后被分发。其中ON_PAUSE、ON_STOP、
     *  ON_DESTROY等事件在对应的回调方法之前被分发。
     */
    class MyLifecyclerObserver : DefaultLifecycleObserver {

        override fun onCreate(owner: LifecycleOwner) {
            super.onCreate(owner)
            Log.d("MyLifecyclerObserver", "onCreate: wyj state:${owner.lifecycle.currentState}" )
        }

        override fun onStart(owner: LifecycleOwner) {
            super.onStart(owner)
            Log.d("MyLifecyclerObserver", "onStart: wyj state:${owner.lifecycle.currentState}")
        }

        override fun onResume(owner: LifecycleOwner) {
            super.onResume(owner)
            Log.d("MyLifecyclerObserver", "onResume: wyj state:${owner.lifecycle.currentState}")
        }

        override fun onPause(owner: LifecycleOwner) {
            super.onPause(owner)
            Log.d("MyLifecyclerObserver", "onPause: wyj state:${owner.lifecycle.currentState}")
        }

        override fun onStop(owner: LifecycleOwner) {
            super.onStop(owner)
            Log.d("MyLifecyclerObserver", "onStop: wyj state:${owner.lifecycle.currentState}")
        }

        override fun onDestroy(owner: LifecycleOwner) {
            super.onDestroy(owner)
            Log.d("MyLifecyclerObserver", "onDestroy: wyj state:${owner.lifecycle.currentState}")
        }
    }
}