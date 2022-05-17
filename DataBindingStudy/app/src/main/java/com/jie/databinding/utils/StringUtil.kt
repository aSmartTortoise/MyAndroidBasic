package com.jie.databinding.utils

import java.util.*

object StringUtil {
    @JvmStatic
    fun toLowerCase(content: String?): String {
        return content?.lowercase(Locale.CHINA) ?: ""
    }
}