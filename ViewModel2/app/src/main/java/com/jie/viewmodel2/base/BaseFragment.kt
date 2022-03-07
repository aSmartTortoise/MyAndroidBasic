package com.jie.databinding.base

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.jie.databinding.extention.getViewBinding
import com.jie.viewmodel2.request.ViewModelUtils

abstract class BaseFragment<VB : ViewDataBinding, VM : ViewModel>(
    private val mShareViewModel: Boolean = false,
    private val mFactory: ViewModelProvider.Factory? = null
) : Fragment(), BaseBinding<VB> {
    protected lateinit var mBinding: VB
        private set
    protected lateinit var mViewModel: VM

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        mBinding = getViewBinding(inflater, container)
        mViewModel = if (mShareViewModel) ViewModelUtils.createActivityViewModel(this, mFactory, 1)
        else ViewModelUtils.createViewModel(this, mFactory, 1)
        return mBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mBinding.initBinding()
    }

    /**
     *  Kotlin双冒号的使用
     *  https://blog.csdn.net/lv_fq/article/details/72869124
     */
    override fun onDestroy() {
        super.onDestroy()
        if (::mBinding.isInitialized) {
            mBinding.unbind()
        }
    }
}