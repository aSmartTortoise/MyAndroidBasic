package com.wyj.imagecache

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Message
import android.util.Log
import android.util.LruCache
import android.widget.Button
import android.widget.ImageView
import okhttp3.*
import java.io.IOException
import java.lang.ref.WeakReference

/**
 *  https://juejin.cn/post/6844903855818276871
 */
class MainActivity : AppCompatActivity() {
    companion object {
        private const val TAG = "MainActivity"
        private const val SUCCESS = 0x0001
        private const val FAIL = 0x0002
    }

    private val imageUrl = "https://cn.bing.com/sa/simg/hpb/LaDigue_EN-CA1115245085_1920x1080.jpg"
    private var imageLoader: MyImageLoader? = null
    private var iv: ImageView? = null
    private var handler: MyHandler? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        imageLoader = MyImageLoader()
        handler = MyHandler(this)
        iv = findViewById<ImageView>(R.id.iv_lrucache)
        findViewById<Button>(R.id.bt_load).setOnClickListener {
            imageLoader?.getBitmap(imageUrl)?.let {
                Log.d(TAG, "onCreate: wyj has memory cache")
                iv?.setImageBitmap(it)
            } ?: downloadBitmap()
        }
    }

    private fun downloadBitmap() {
        Log.d(TAG, "downloadBitmap: wyj 从网络下载图片")
        val okHttpClient = OkHttpClient()
        val request: Request = Request.Builder()
            .url(imageUrl)
            .build()
        val call = okHttpClient.newCall(request)
        call.enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                Log.d(TAG, "onFailure: wyj download image failed.")
                handler?.obtainMessage(FAIL)?.let { handler?.sendMessage(it) }
            }

            override fun onResponse(call: Call, response: Response) {
                val bytes = response.body()?.bytes()
                val message = handler?.obtainMessage()?.apply {
                    what = SUCCESS
                    obj = bytes
                    handler?.sendMessage(this)
                }
            }

        })
    }

    private fun onDownloaded(bytes: ByteArray) {
        val bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
        imageLoader?.addBitmap(imageUrl, bitmap)
        iv?.setImageBitmap(bitmap)
    }

    @SuppressLint("HandlerLeak")
    class MyHandler(activity: MainActivity): Handler() {
        private var reference: WeakReference<MainActivity>
        init {
            reference = WeakReference(activity)
        }

        override fun handleMessage(msg: Message) {
            super.handleMessage(msg)
            when(msg.what) {
                SUCCESS -> {
                    val activity = reference.get()
                    activity?.let {
                        if (!it.isFinishing) {
                            if (msg.obj is ByteArray) {
                                it.onDownloaded(msg.obj as ByteArray)
                            }
                        }
                    }
                }
                FAIL -> {
                    Log.d(TAG, "handleMessage: wyj 图片下载失败")
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        handler?.let {
            it.removeCallbacksAndMessages(null)
            handler = null
        }
    }
}