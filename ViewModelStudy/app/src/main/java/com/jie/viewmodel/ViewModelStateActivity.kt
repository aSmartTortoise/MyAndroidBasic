package com.jie.viewmodel

import android.content.res.Configuration
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.TextView
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class ViewModelStateActivity : AppCompatActivity() {
    companion object {
        const val TAG = "ViewModelStateActivity"
    }
    private lateinit var stateViewModel: StateViewModel
    private var tvCount : TextView? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_view_model_state)
        stateViewModel = ViewModelProvider(this).get(StateViewModel::class.java)
        Log.d(TAG, "onCreate: wyj stateViewModel:$stateViewModel")
        tvCount = findViewById(R.id.tv_count)
        findViewById<View>(R.id.btn_start).setOnClickListener {
            val count = stateViewModel.countData.value
            stateViewModel.setCount(count?.plus(1) ?: 0)
            tvCount?.text = stateViewModel.countData.value!!.toString()
        }
        if (savedInstanceState != null) {
            tvCount?.text = stateViewModel.countData.value!!.toString()
        }
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
    }

    override fun onDestroy() {
        super.onDestroy()
    }
}