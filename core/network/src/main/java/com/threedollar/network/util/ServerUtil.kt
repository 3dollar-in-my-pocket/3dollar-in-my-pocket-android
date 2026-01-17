package com.threedollar.network.util

import com.google.gson.Gson
import com.google.gson.JsonSyntaxException
import com.threedollar.common.base.BaseResponse
import com.threedollar.network.data.ErrorResponse
import com.threedollar.network.result.ResponseBodyNullPointerException
import com.threedollar.network.result.ApiException
import com.threedollar.network.result.WeakNetworkException
import kotlinx.coroutines.CancellationException
import okio.IOException
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

suspend inline fun <T> runApi(
    crossinline call: suspend () -> Response<BaseResponse<T>>
): Result<T> {
    try {
        val response = call.invoke()

        if (!response.isSuccessful) {
            val apiException = ApiException.from(response.errorResponseOrNull())
            return Result.failure(apiException)
        }

        val body = response.body()
        val data = body?.data ?: return Result.failure(ResponseBodyNullPointerException())

        return Result.success(data)
    } catch (e: CancellationException) {
        throw e
    } catch (e: Exception) {
        return Result.failure(
            exception = if (e is IOException) {
                WeakNetworkException(e)
            } else {
                e
            }
        )
    }
}

