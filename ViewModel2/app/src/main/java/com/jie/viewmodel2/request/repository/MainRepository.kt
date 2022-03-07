package com.jie.viewmodel2.request.repository

import com.jie.viewmodel2.bean.Weather
import com.jie.viewmodel2.remote.CResponse
import com.jie.viewmodel2.remote.ServiceApi

class MainRepository() : BaseRepository() {
    suspend fun getWhether(area: String): CResponse<Weather> {
        return ServiceApi.mService.getWeather(area)
    }
}