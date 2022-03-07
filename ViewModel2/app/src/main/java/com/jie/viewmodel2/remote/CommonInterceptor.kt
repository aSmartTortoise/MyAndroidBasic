package com.jie.viewmodel2.remote

import com.jie.viewmodel2.constant.HttpConstant
import com.jie.viewmodel2.constant.HttpConstant.SHOW_API_APPID
import com.jie.viewmodel2.constant.HttpConstant.SHOW_API_SIGN
import okhttp3.Interceptor
import okhttp3.Response

class CommonInterceptor : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val oldRequest = chain.request()
        val httpUrl = oldRequest.url()
        val host = httpUrl.host()
        if (HttpConstant.SERVER_HOST != host) {
            return chain.proceed(oldRequest)
        }

        val urlBuilder = httpUrl.newBuilder()
        urlBuilder.addQueryParameter("showapi_appid", SHOW_API_APPID)
        urlBuilder.addQueryParameter("showapi_sign", SHOW_API_SIGN)
        val request = oldRequest.newBuilder()
            .url(urlBuilder.build())
            .build()
        return chain.proceed(request)
    }

}