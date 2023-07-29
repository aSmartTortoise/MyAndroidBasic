package com.jie.viewmodel

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import androidx.activity.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider

/**
 *  https://developer.android.com/topic/libraries/architecture/viewmodel?hl=zh-cn
 *  http://liuwangshu.cn/application/jetpack/6-viewmodel.html
 *  https://segmentfault.com/a/1190000040202908
 *  ViewModel以感知声明周期的形式存储和管理视图相关数据。
 *  1 ViewModel的生命周期
 *      当旋转屏幕的时候，Activity会被销毁并重新创建，但是ViewModel的声明周期不会变化。
 *  2 ViewModel的初始化
 *
 *  3 ViewModel概览
 *      ViewModel以感知生命周期的方式存储和管理界面数据，它可以使得数据在界面配置发生变化的时候
 *  得以留存。
 *      当Activity/Fragment被系统销毁的时候，就要重新创建Activity/Fragment，这时候就涉及到了数据
 *      保存和恢复。通常使用onSaveInstanceState和onRestoreSaveState来处理数据的保存与恢复。但是
 *  这两个方法只能处理数据较小，且数据是序列化的情况。当遇到数据较大，需要用到ViewModel，另外ViewModel
 *  可以将业务逻辑从Activity/Fragment中分离出来。
 *
 *
 *
 *
 */
class MainActivity : AppCompatActivity() {
    companion object {
        const val TAG = "MainActivity"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val viewModel = ViewModelProvider.NewInstanceFactory().create(MyViewModel::class.java)
        viewModel.getName()?.observe(this, object : Observer<String> {
            override fun onChanged(t: String?) {
                Log.d(TAG, "onChanged: wyj t:$t")
            }

        })
        initViewModel()

        findViewById<Button>(R.id.btn_view_model_share).setOnClickListener { v ->
            Intent(this@MainActivity, ViewModleShareActivity::class.java).run {
                startActivity(this)
            }
        }

        findViewById<Button>(R.id.btn_view_model_state).setOnClickListener { v ->
            Intent(this@MainActivity, ViewModelStateActivity::class.java).run {
                startActivity(this)
            }
        }
    }

    private fun initViewModel() {
        // 1. 使用Android 工厂创建ViewModel，每次都会创建新的Model，不受ViewModelStore的管控
        // 非特殊情况禁止使用这种方式创建ViewModel。
        val factoryViewModel =
            ViewModelProvider.AndroidViewModelFactory.getInstance(application).create(
                AndroidFactoryViewModel::class.java
            )
        factoryViewModel.print()
        // 2 使用简单工厂创建ViewModel实例。每次都会创建新Model，不受ViewModelStore管控，非特殊
        //情况下禁止使用这种方式创建ViewModel。
        val simpleFactoryViewModel =
            ViewModelProvider.NewInstanceFactory().create(SimpleFactoryViewModel::class.java)
        simpleFactoryViewModel.print()
        // 3 自定义AndroidViewModelFactory，获取AndroidViewModle，创建的Modle可以复用，不会被重新创建。

        val customAndroidViewModel = ViewModelProvider(
            viewModelStore,
            CustomAndroidViewModelFactory(
                application,
                "自定义AndroidViewModelFactory来获取AndroidViewModel实例"
            )
        ).get(CustomAndroidViewModel::class.java)
        customAndroidViewModel.print()

        // 4 自定义简单ViewModel factory获取ViewModel实例，创建的Modle可以复用，不会被重新创建。
        val customSimpleViewModel = ViewModelProvider(
            viewModelStore,
            CustomSimpleViewModelFactory(application, "自定义简单ViewModel 工厂，获取ViewModel实例")
        ).get(CustomSimpleViewModel::class.java)
        customSimpleViewModel.print()

        val simpleViewModel2 = ViewModelProvider(this).get(CustomSimpleViewModel::class.java)
        Log.d(
            TAG, "initViewModel: wyj ViewModelProvider一个参数的构造方法，获取ViewModelProvider的实例" +
                    "， 进而获取ViewModel的实例"
        )
        simpleViewModel2.print()

        // 5 google官方提供了activity-ktx库，使用它，我们可以利用委托机制获取ViewModel的实例。
        val enTrustViewModel: EnTrustViewModel by viewModels<EnTrustViewModel> {
            ViewModelProvider.NewInstanceFactory()
        }
        enTrustViewModel.print()
    }
}