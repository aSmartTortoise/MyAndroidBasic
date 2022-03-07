package com.jie.viewmodel2.remote

import com.jie.viewmodel2.constant.HttpConstant
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory

object ServiceApi {
    val mService: CoroutineService by lazy(LazyThreadSafetyMode.SYNCHRONIZED) {
        build()
    }

    private fun build(): CoroutineService {
        val retrofit = Retrofit.Builder().apply {
            baseUrl(HttpConstant.HTTP_SERVER)
            client(OkHttpClientManager.mClient)
            addConverterFactory(ScalarsConverterFactory.create())
            addConverterFactory(GsonConverterFactory.create())
        }.build()
        return retrofit.create(CoroutineService::class.java)
    }
}