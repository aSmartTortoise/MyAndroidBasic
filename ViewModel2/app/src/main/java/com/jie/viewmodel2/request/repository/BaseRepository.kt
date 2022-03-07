package com.jie.viewmodel2.request.repository

import com.jie.viewmodel2.constant.HttpConstant
import com.jie.viewmodel2.remote.CResponse

open class BaseRepository {
    suspend fun <T : Any> handleResponse(
        response: CResponse<T>, onSuccess: suspend () -> Unit,
        errorBlock: suspend () -> Unit
    ) {
        when{
            response == null -> errorBlock()
            response.code == HttpConstant.OK -> onSuccess()
            else -> errorBlock
        }
    }
}