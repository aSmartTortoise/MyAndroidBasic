package com.jie.viewmodel

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
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

        // 5 google官方提供了activity-ktx库，使用它，我们可以利用委托机制获取ViewModel的实例。
        val enTrustViewModel: EnTrustViewModel by viewModels<EnTrustViewModel> {
            ViewModelProvider.NewInstanceFactory()
        }
        enTrustViewModel.print()


    }


}