package com.wyj.easyfloat.`interface`

import android.animation.Animator
import android.view.View
import android.view.WindowManager
import com.wyj.easyfloat.SidePattern

/**
 *  author : jie wang
 *  date : 2024/2/19 10:13
 *  description : 系统浮窗的出入动画
 */
interface OnFloatAnimator {
    fun enterAnim(
        view: View,
        params: WindowManager.LayoutParams,
        windowManager: WindowManager,
        sidePattern: SidePattern
    ): Animator? = null

    fun exitAnim(
        view: View,
        params: WindowManager.LayoutParams,
        windowManager: WindowManager,
        sidePattern: SidePattern
    ): Animator? = null
}