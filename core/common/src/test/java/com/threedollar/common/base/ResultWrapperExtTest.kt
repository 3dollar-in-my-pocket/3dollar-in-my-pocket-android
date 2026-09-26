package com.threedollar.common.base

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.ResponseBody.Companion.toResponseBody
import org.junit.Assert.assertEquals
import org.junit.Test
import retrofit2.Response

class ResultWrapperExtTest {

    private val json = "application/json".toMediaType()

    // TH-1355 TC1

    @Test
    fun `TH1355_TC1_2xx이고_resultCode가_빈문자열이면_data를_Success로_돌려준다`() = runBlocking<Unit> {
        // Given
        val response = Response.success(BaseResponse(ok = true, data = "payload", resultCode = ""))

        // When
        val result = response.toResultWrapper(Dispatchers.Unconfined)

        // Then
        assertEquals(ResultWrapper.Success("payload"), result)
    }

    @Test
    fun `TH1355_TC1_2xx이고_resultCode가_null이면_data를_Success로_돌려준다`() = runBlocking<Unit> {
        // Given
        val response = Response.success(BaseResponse(ok = true, data = "payload", resultCode = null))

        // When
        val result = response.toResultWrapper(Dispatchers.Unconfined)

        // Then
        assertEquals(ResultWrapper.Success("payload"), result)
    }

    // TH-1355 TC2

    @Test
    fun `TH1355_TC2_2xx여도_resultCode가_숫자면_그_숫자를_code로_GenericError를_돌려준다`() = runBlocking<Unit> {
        // Given
        val response = Response.success(
            BaseResponse(ok = false, data = null as String?, message = "이미 탈퇴한 유저", resultCode = "4001")
        )

        // When
        val result = response.toResultWrapper(Dispatchers.Unconfined)

        // Then
        assertEquals(ResultWrapper.GenericError(4001, "이미 탈퇴한 유저"), result)
    }

    // TH-1355 TC3

    @Test
    fun `TH1355_TC3_2xx여도_resultCode가_숫자가_아니면_code없이_GenericError를_돌려준다`() = runBlocking<Unit> {
        // Given
        val response = Response.success(
            BaseResponse(ok = false, data = null as String?, message = "알 수 없는 오류", resultCode = "E01")
        )

        // When
        val result = response.toResultWrapper(Dispatchers.Unconfined)

        // Then
        assertEquals(ResultWrapper.GenericError(null, "알 수 없는 오류"), result)
    }

    // TH-1355 TC4

    @Test
    fun `TH1355_TC4_HTTP에러면_상태코드와_에러바디의_message로_GenericError를_돌려준다`() = runBlocking<Unit> {
        // Given
        val errorBody = """{"ok":false,"message":"존재하지 않는 유저입니다"}""".toResponseBody(json)
        val response = Response.error<BaseResponse<String>>(404, errorBody)

        // When
        val result = response.toResultWrapper(Dispatchers.Unconfined)

        // Then
        assertEquals(ResultWrapper.GenericError(404, "존재하지 않는 유저입니다"), result)
    }

    // TH-1355 TC5

    @Test
    fun `TH1355_TC5_HTTP에러에_에러바디가_비었으면_응답메시지로_GenericError를_돌려준다`() = runBlocking<Unit> {
        // Given
        val response = Response.error<BaseResponse<String>>(503, "".toResponseBody(json))

        // When
        val result = response.toResultWrapper(Dispatchers.Unconfined)

        // Then
        assertEquals(ResultWrapper.GenericError(503, response.message()), result)
    }

    @Test
    fun `TH1355_TC5_HTTP에러에_에러바디가_JSON이_아니면_응답메시지로_GenericError를_돌려준다`() = runBlocking<Unit> {
        // Given
        val response = Response.error<BaseResponse<String>>(502, "<html>Bad Gateway</html>".toResponseBody(json))

        // When
        val result = response.toResultWrapper(Dispatchers.Unconfined)

        // Then
        assertEquals(ResultWrapper.GenericError(502, response.message()), result)
    }
}
