package com.jie.viewmodel2

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.activity.viewModels
import androidx.lifecycle.ViewModelProvider
import com.jie.viewmodel2.request.viewmodel.MainViewModel

/**
 * 封装ViewModel
 * https://juejin.cn/post/6962921719522656287/
 */
class MainActivity : AppCompatActivity() {
    companion object {
        const val TAG = "MainActivity"
    }

    // 2通过activity-ktx扩展库中的ComponentActivity的内联扩展函数，获取ViewModel
    //实例
    private val mViewModel: MainViewModel by viewModels<MainViewModel>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        // 1 通过ViewModelProvider获取ViewModel实例
        val viewModel = ViewModelProvider(this).get(MainViewModel::class.java)
        viewModel.mUsers.observe(this) {
            Log.d(TAG, "onCreate: wyj user:$it")
        }

        mViewModel.mUsers.observe(this) {
            Log.d(TAG, "onCreate: wyj 111 user:$it")
        }
    }
}