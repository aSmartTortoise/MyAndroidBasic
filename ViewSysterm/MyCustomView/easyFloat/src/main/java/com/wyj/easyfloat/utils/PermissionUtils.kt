package com.wyj.easyfloat.utils

import android.app.Activity
import android.app.Fragment
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.provider.Settings
import android.util.Log

/**
 *  author : jie wang
 *  date : 2024/2/19 10:44
 *  description : 悬浮窗权限工具类
 */
object PermissionUtils {
    private const val TAG = "PermissionUtils"

    /**
     * 检测是否有悬浮窗权限
     * 6.0 版本之后由于 google 增加了对悬浮窗权限的管理，所以方式就统一了
     */
    @JvmStatic
    fun checkPermission(context: Context): Boolean =
        commonROMPermissionCheck(context)

    /**
     * 6.0以后，通用悬浮窗权限检测
     * 但是魅族6.0的系统这种方式不好用，需要单独适配一下
     */
    private fun commonROMPermissionCheck(context: Context): Boolean {
        var result = true
        if (Build.VERSION.SDK_INT >= 23) try {
            val clazz = Settings::class.java
            val canDrawOverlays =
                clazz.getDeclaredMethod("canDrawOverlays", Context::class.java)
            result = canDrawOverlays.invoke(null, context) as Boolean
        } catch (e: Exception) {
            Log.e(TAG, "commonROMPermissionCheck e:$e")
        }
        return result
    }

}