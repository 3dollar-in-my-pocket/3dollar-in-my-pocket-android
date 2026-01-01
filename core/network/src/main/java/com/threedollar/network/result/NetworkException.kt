package com.threedollar.network.result

import com.threedollar.network.data.ErrorResponse

class ApiException(
    val error: ApiError = ApiError.UNKNOWN,
    override val message: String? = null
) : Exception("$error - $message") {

    companion object {
        fun from(response: ErrorResponse?): ApiException = ApiException(
            error = ApiError.from(response?.resultCode),
            message = response?.message
        )
    }
}

class ResponseBodyNullPointerException() : NullPointerException()

class WeakNetworkException(
    override val cause: Throwable?
) : Exception(cause)

enum class ApiError(
    val rawCode: String?
) {
    INVALID_PARAMETER("BR000"),
    UNAUTHORIZED("UA000"),
    TOO_MAY_REQUESTS("TM000"),
    SERVER_INTERNAL("IS000"),
    ALREADY_EXISTS_NICKNAME("CF001"),
    UNKNOWN(null);

    companion object {
        fun from(rawCode: String?): ApiError = try {
            ApiError.entries.find { it.rawCode == rawCode } ?: UNKNOWN
        } catch (_: Exception) {
            UNKNOWN
        }
    }
}
