package com.jie.databinding.base

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.ViewDataBinding
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.jie.databinding.extention.createViewModel
import com.jie.databinding.extention.getViewBinding
import com.jie.viewmodel2.request.ViewModelUtils

abstract class BaseActivity<VB : ViewDataBinding, VM : ViewModel>(private val mFactory: ViewModelProvider.Factory? = null) :
    AppCompatActivity(), BaseBinding<VB> {
    internal val mBinding: VB by lazy(mode = LazyThreadSafetyMode.NONE) {
        getViewBinding(layoutInflater)
    }
    protected lateinit var mViewModle: ViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(mBinding.root)
        mViewModle = ViewModelUtils.createViewModel(this, mFactory, 1)
        mBinding.initBinding()
        initObserve()
    }

    abstract fun initObserve()
}