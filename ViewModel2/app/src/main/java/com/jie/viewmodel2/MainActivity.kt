package com.jie.viewmodel2

import android.util.Log
import com.jie.databinding.base.BaseActivity
import com.jie.viewmodel2.databinding.ActivityMainBinding
import com.jie.viewmodel2.request.provideMainViewModelFactory
import com.jie.viewmodel2.request.viewmodel.MainViewModel

/**
 * 封装ViewModel
 * https://juejin.cn/post/6962921719522656287/
 */
class MainActivity : BaseActivity<ActivityMainBinding, MainViewModel>(provideMainViewModelFactory()) {
    companion object {
        const val TAG = "MainActivity"
    }

    override fun initObserve() {
        (mViewModle as MainViewModel).mUsers.observe(this) {
            Log.d(TAG, "initObserve: wyj user:$it")
        }
    }

    override fun ActivityMainBinding.initBinding() {

    }
}