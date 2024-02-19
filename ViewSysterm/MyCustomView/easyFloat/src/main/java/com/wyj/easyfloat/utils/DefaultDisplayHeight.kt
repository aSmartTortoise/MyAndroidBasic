package com.wyj.easyfloat.utils

import android.content.Context
import com.wyj.easyfloat.`interface`.OnDisplayHeight

/**
 *  author : jie wang
 *  date : 2024/2/19 10:24
 *  description :
 */
class DefaultDisplayHeight : OnDisplayHeight {
    override fun getDisplayRealHeight(context: Context) =
        DisplayUtils.rejectedNavHeight(context)
}