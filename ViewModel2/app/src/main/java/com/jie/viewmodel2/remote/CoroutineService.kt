package com.jie.viewmodel2.remote

import com.jie.viewmodel2.bean.Weather
import retrofit2.http.GET
import retrofit2.http.Query

interface CoroutineService {
    @GET("/9-2")
    suspend fun getWeather(@Query("area") area: String): CResponse<Weather>
}