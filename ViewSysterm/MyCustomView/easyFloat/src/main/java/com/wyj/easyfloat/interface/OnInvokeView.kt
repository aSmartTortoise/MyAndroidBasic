package com.wyj.easyfloat.`interface`

import android.view.View

/**
 *  author : jie wang
 *  date : 2024/2/19 10:06
 *  description : 设置浮窗内容的接口
 */
interface OnInvokeView {
    /**
     * 设置浮窗布局的具体内容
     *
     * @param view 浮窗布局
     */
    fun invoke(view: View)
}