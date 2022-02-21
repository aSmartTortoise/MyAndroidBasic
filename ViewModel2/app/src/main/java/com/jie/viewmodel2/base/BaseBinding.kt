package com.jie.databinding.base

import androidx.databinding.ViewDataBinding

interface BaseBinding<VB: ViewDataBinding> {
    fun VB.initBinding()
}