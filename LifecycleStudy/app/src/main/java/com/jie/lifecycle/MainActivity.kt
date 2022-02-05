package com.jie.lifecycle

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.lifecycle.*

class MainActivity : AppCompatActivity() {
    companion object {
        const val TAG = "MainActivity"
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        Log.d(TAG, "onCreate: wyj add observer")
        lifecycle.addObserver(MyLifecyclerObserver())
    }

    class MyLifecyclerObserver : DefaultLifecycleObserver {

        override fun onCreate(owner: LifecycleOwner) {
            super.onCreate(owner)
            Log.d(TAG, "onCreate: wyj")
        }

        override fun onStart(owner: LifecycleOwner) {
            super.onStart(owner)
            Log.d(TAG, "onStart: wyj")
        }

        override fun onResume(owner: LifecycleOwner) {
            super.onResume(owner)
            Log.d(TAG, "onResume: wyj")
        }

        override fun onPause(owner: LifecycleOwner) {
            super.onPause(owner)
            Log.d(TAG, "onPause: wyj")
        }

        override fun onStop(owner: LifecycleOwner) {
            super.onStop(owner)
            Log.d(TAG, "onStop: wyj")
        }

        override fun onDestroy(owner: LifecycleOwner) {
            super.onDestroy(owner)
            Log.d(TAG, "onDestroy: wyj")
        }
    }
}