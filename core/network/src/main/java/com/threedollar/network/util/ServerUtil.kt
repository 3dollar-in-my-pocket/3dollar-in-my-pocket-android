package com.threedollar.network.util

import com.google.gson.Gson
import com.google.gson.JsonSyntaxException
import com.threedollar.common.base.BaseResponse
import com.threedollar.network.data.ErrorResponse
import retrofit2.Response

fun <T> apiResult(response: Response<BaseResponse<T>>): BaseResponse<T> =
    if (response.isSuccessful) {
        response.body()!!
    } else {
        val errorResponse = try {
            Gson().fromJson(response.errorBody()?.string(), ErrorResponse::class.java)
        } catch (_: JsonSyntaxException) {
            null
        }

        BaseResponse(
            ok = errorResponse?.ok ?: false,
            data = null,
            message = errorResponse?.message,
            resultCode = errorResponse?.resultCode,
            error = errorResponse?.error
        )
    }

fun Response<*>.errorResponseOrNull(): ErrorResponse? = try {
    Gson().fromJson(errorBody()?.string(), ErrorResponse::class.java)
} catch (e: Exception) {
    null
}
