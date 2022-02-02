package com.jie.databinding.ui.activity

import android.util.Log
import com.jie.databinding.base.BaseActivity
import com.jie.databinding.databinding.ActivityMainBinding

/**
 *  封装DataBinding
 *  https://juejin.cn/post/6957608813809795108
 */
class MainActivity : BaseActivity<ActivityMainBinding>() {
    override fun ActivityMainBinding.initBinding() {
        Log.d("MainActivity", "initBinding: btn:${btn.text}")
    }
}