package com.jie.databinding.utils;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;

import java.util.Locale;

import androidx.databinding.BindingConversion;

public class StringUtils {
    public static String toLowerCase(String str) {
        if (str == null) return "null";
        return str.toLowerCase(Locale.CHINA);
    }

    /**
     *  带有BindingConversion注解的方法，会将工程中布局文件所有以@{String}方式引用的变量加上后缀
     *  -conversionString
     */

//    @BindingConversion
//    public static String conversionString(String text) {
//        return text + "-conversionString";
//    }

    /**
     *  将xml中控件中的红色、蓝色 字符串转换为Drawable何Color类型。
     * @param str
     * @return
     */
    @BindingConversion
    public static Drawable convertionStringToDrawable(String str) {
        if (str.equals("红色")) {
            return new ColorDrawable(Color.parseColor("#FF4081"));
        }

        if (str.equals("蓝色")) {
            return new ColorDrawable(Color.parseColor("#3F51B5"));
        }

        return new ColorDrawable(Color.parseColor("#344567"));
    }

    @BindingConversion
    public static int conversionStringToColor(String str) {
        if (str.equals("红色")) {
            return Color.parseColor("#FF4081");
        }
        if (str.equals("蓝色")) {
            return Color.parseColor("#3F51B5");
        }
        return Color.parseColor("#344567");
    }
}
