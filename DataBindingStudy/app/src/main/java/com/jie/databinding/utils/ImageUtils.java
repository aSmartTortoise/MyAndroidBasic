package com.jie.databinding.utils;

import android.util.Log;
import android.widget.ImageView;

import androidx.databinding.BindingAdapter;

public class ImageUtils {
    @BindingAdapter("url")
    public static void loadImage(ImageView view, String url) {
        Log.d("ImageUtils", "loadImage: url:" + url);
    }

}
