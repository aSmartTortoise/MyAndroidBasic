package com.jie.databinding.utils;

import java.util.Locale;

public class StringUtils {
    public static String toLowerCase(String str) {
        if (str == null) return "null";
        return str.toLowerCase(Locale.CHINA);
    }
}
