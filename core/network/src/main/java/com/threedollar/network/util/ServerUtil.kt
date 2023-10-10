package com.threedollar.network.util

import com.google.gson.Gson
import com.threedollar.common.base.BaseResponse
import com.threedollar.network.data.ErrorResponse
import retrofit2.Response

fun <T> apiResult(response: Response<BaseResponse<T>>) : BaseResponse<T> =
    if (response.isSuccessful) {
        response.body()!!
    } else {
        val errorResponse = Gson().fromJson(response.errorBody()?.string(), ErrorResponse::class.java)
        BaseResponse(
            ok = errorResponse.ok ?: false,
            data = null,
            message = errorResponse.message,
            resultCode = errorResponse.resultCode,
            error = errorResponse.error
        )
    }