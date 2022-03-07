package com.jie.viewmodel2.remote

import com.jie.viewmodel2.BuildConfig
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor

object OkHttpClientManager {

    val mClient: OkHttpClient by lazy(LazyThreadSafetyMode.SYNCHRONIZED) {
        buildClient()
    }

    private fun buildClient(): OkHttpClient {
        val logging = HttpLoggingInterceptor()
        logging.level =
            if (BuildConfig.DEBUG) HttpLoggingInterceptor.Level.BODY else HttpLoggingInterceptor.Level.NONE
        return OkHttpClient.Builder().apply {
            addInterceptor(CommonInterceptor())
            addInterceptor(logging)
            followSslRedirects(true)
        }.build()
    }
}