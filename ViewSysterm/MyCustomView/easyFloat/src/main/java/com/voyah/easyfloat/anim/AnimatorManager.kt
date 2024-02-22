package com.voyah.easyfloat.anim

import android.animation.Animator
import android.view.View
import android.view.WindowManager
import com.voyah.easyfloat.FloatConfig

/**
 *  author : jie wang
 *  date : 2024/2/19 11:29
 *  description : App浮窗的出入动画管理类，只需传入具体的动画实现类（策略模式）
 */
internal class AnimatorManager(
    private val view: View,
    private val params: WindowManager.LayoutParams,
    private val windowManager: WindowManager,
    private val config: FloatConfig
) {

    fun enterAnim(): Animator? =
        config.floatAnimator?.enterAnim(view, params, windowManager, config.sidePattern)

    fun exitAnim(): Animator? =
        config.floatAnimator?.exitAnim(view, params, windowManager, config.sidePattern)
}