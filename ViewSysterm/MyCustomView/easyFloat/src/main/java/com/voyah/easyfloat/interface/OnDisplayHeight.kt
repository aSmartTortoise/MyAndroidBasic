package com.voyah.easyfloat.`interface`

import android.content.Context

/**
 *  author : jie wang
 *  date : 2024/2/19 10:22
 *  description : 通过接口获取屏幕的有效显示高度
 */
interface OnDisplayHeight {
    /**
     * 获取屏幕有效的显示高度，不包含虚拟导航栏
     *
     * @param context ApplicationContext
     * @return 高度值（int类型）
     */
    fun getDisplayRealHeight(context: Context): Int
}