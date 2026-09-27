package com.threedollar.common.base

import com.google.gson.Gson
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.Response

/**
 * 이미 받은 Retrofit 응답을 [ResultWrapper]로 변환한다.
 *
 * 네트워크 호출 자체에서 난 예외는 여기서 잡지 않는다. 호출한 쪽 코루틴으로 그대로 전파된다.
 * [ResultWrapper.NetworkError]가 되는 것은 변환 도중(에러 바디 읽기 등)에 난 예외뿐이다.
 *
 * - HTTP 2xx, `resultCode` 비어 있음 → [ResultWrapper.Success]
 * - HTTP 2xx, `resultCode` 가 숫자 → [ResultWrapper.GenericError] (code = resultCode)
 * - HTTP 2xx, `resultCode` 가 숫자 아님 → [ResultWrapper.GenericError] (code = null)
 * - 그 외 HTTP → [ResultWrapper.GenericError] (code = HTTP 상태 코드, msg = 에러 바디의 message)
 */
suspend fun <T> Response<BaseResponse<T>>.toResultWrapper(
    dispatcher: CoroutineDispatcher = Dispatchers.IO,
): ResultWrapper<T?> = withContext(dispatcher) {
    try {
        if (isSuccessful) toSuccessResult() else toErrorResult()
    } catch (throwable: Throwable) {
        ResultWrapper.NetworkError
    }
}

/**
 * [ResultWrapper.Success]의 값만 변환하고, 에러는 그대로 둔다.
 */
inline fun <T, R> ResultWrapper<T>.map(transform: (T) -> R): ResultWrapper<R> = when (this) {
    is ResultWrapper.Success -> ResultWrapper.Success(transform(value))
    is ResultWrapper.GenericError -> this
    ResultWrapper.NetworkError -> ResultWrapper.NetworkError
}

private fun <T> Response<BaseResponse<T>>.toSuccessResult(): ResultWrapper<T?> {
    val body = body()
    val resultCode = body?.resultCode
    return when {
        resultCode.isNullOrEmpty() -> ResultWrapper.Success(body?.data)
        resultCode.all(Char::isDigit) -> ResultWrapper.GenericError(resultCode.toInt(), body?.message)
        else -> ResultWrapper.GenericError(null, body?.message)
    }
}

private fun <T> Response<BaseResponse<T>>.toErrorResult(): ResultWrapper<T?> {
    val errorMessage = try {
        val errorBodyString = errorBody()?.string()
        if (!errorBodyString.isNullOrEmpty()) {
            Gson().fromJson(errorBodyString, BaseResponse::class.java).message ?: message()
        } else {
            message()
        }
    } catch (e: Exception) {
        message()
    }
    return ResultWrapper.GenericError(code(), errorMessage)
}
