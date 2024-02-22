package com.voyah.ai.window

import android.app.Application
import android.util.Log

/**
 *  author : jie wang
 *  date : 2024/2/22 15:55
 *  description :
 */
class VoiceCardApplication: Application() {

    companion object {
        const val TAG = "VoiceCardApplication"
    }

    override fun onCreate() {
        super.onCreate()
        Log.d(TAG, "onCreate")
    }
}