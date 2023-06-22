package com.wyj.imagecache

import android.graphics.Bitmap
import androidx.collection.LruCache

class MyImageLoader {
    private var cache: LruCache<String, Bitmap>

    init {
        val maxMemory = Runtime.getRuntime().maxMemory()
        cache = object : LruCache<String, Bitmap>((maxMemory / 8).toInt()) {
            override fun sizeOf(key: String, value: Bitmap): Int {
                return value.byteCount
            }
        }
    }

    public fun getBitmap(key: String): Bitmap? = cache.get(key)

    fun addBitmap(key: String, bitmap: Bitmap) {
        if (getBitmap(key) == null) {
            cache.put(key, bitmap)
        }
    }

    fun removeBitmap(key: String): Boolean = cache.remove(key) == null
}