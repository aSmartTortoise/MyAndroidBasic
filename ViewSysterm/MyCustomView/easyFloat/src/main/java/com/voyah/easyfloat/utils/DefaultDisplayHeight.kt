package com.voyah.easyfloat.utils

import android.content.Context
import com.voyah.easyfloat.`interface`.OnDisplayHeight

/**
 *  author : jie wang
 *  date : 2024/2/19 10:24
 *  description : 获取屏幕有效高度的实现类
 */
class DefaultDisplayHeight : OnDisplayHeight {
    override fun getDisplayRealHeight(context: Context) =
        DisplayUtils.rejectedNavHeight(context)
}