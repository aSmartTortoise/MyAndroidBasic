package com.jie.databinding.base

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.ViewDataBinding
import androidx.lifecycle.ViewModel
import com.jie.databinding.extention.createViewModel
import com.jie.databinding.extention.getViewBinding

abstract class BaseActivity<VB: ViewDataBinding, VM: ViewModel> : AppCompatActivity(), BaseBinding<VB> {
    internal val mBinding: VB by lazy(mode = LazyThreadSafetyMode.NONE) {
        getViewBinding(layoutInflater)
    }
    protected lateinit var mViewModle: ViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(mBinding.root)
        mViewModle = createViewModel(1)
        mBinding.initBinding()
        initObserve()

    }

    abstract fun initObserve()
}