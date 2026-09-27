package com.threedollar.data.login

import com.google.gson.Gson
import com.threedollar.common.base.BaseResponse
import com.threedollar.common.base.ResultWrapper
import com.threedollar.data.fake.FakeLoginDataSource
import com.threedollar.data.fake.FakeLoginRemoteDataSource
import com.threedollar.data.login.repository.LoginRepositoryImpl
import com.threedollar.domain.login.model.SignUserModel
import com.threedollar.network.data.auth.LoginRequest
import com.threedollar.network.data.auth.SignUpRequest
import com.threedollar.network.data.auth.SignUser
import com.threedollar.network.request.PushInformationRequest
import kotlinx.coroutines.runBlocking
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.ResponseBody.Companion.toResponseBody
import org.junit.Assert.assertEquals
import org.junit.Assert.assertThrows
import org.junit.Test
import retrofit2.Response
import java.io.IOException

class LoginRepositoryImplTest {

    private val json = "application/json".toMediaType()

    // TH-1355 TC6

    @Test
    fun `TH1355_TC6_로그인이_404면_GenericError_404가_그대로_전달된다`() = runBlocking<Unit> {
        // Given
        val dataSource = FakeLoginDataSource(
            loginResult = { Response.error(404, """{"message":"가입되지 않은 유저"}""".toResponseBody(json)) }
        )
        val repository = LoginRepositoryImpl(FakeLoginRemoteDataSource, dataSource)

        // When
        val result = repository.login(socialType = "KAKAO", token = "social-token")

        // Then
        assertEquals(ResultWrapper.GenericError(404, "가입되지 않은 유저"), result)
    }

    // TH-1355 TC7

    @Test
    fun `TH1355_TC7_로그인에_성공하면_SignUserModel로_매핑되고_요청값이_그대로_전달된다`() = runBlocking<Unit> {
        // Given
        val dataSource = FakeLoginDataSource(
            loginResult = { Response.success(BaseResponse(ok = true, data = SignUser(token = "access", userId = 7))) }
        )
        val repository = LoginRepositoryImpl(FakeLoginRemoteDataSource, dataSource)

        // When
        val result = repository.login(socialType = "KAKAO", token = "social-token")

        // Then
        assertEquals(ResultWrapper.Success(SignUserModel(token = "access", userId = 7)), result)
        assertEquals(LoginRequest(socialType = "KAKAO", token = "social-token"), dataSource.lastLoginRequest)
    }

    @Test
    fun `TH1355_TC7_서버가_token을_null로_주면_SignUserModel의_token도_null이다`() = runBlocking<Unit> {
        // Given
        val signUserWithNullToken = Gson().fromJson("""{"token":null,"userId":7}""", SignUser::class.java)
        val dataSource = FakeLoginDataSource(
            loginResult = { Response.success(BaseResponse(ok = true, data = signUserWithNullToken)) }
        )
        val repository = LoginRepositoryImpl(FakeLoginRemoteDataSource, dataSource)

        // When
        val result = repository.login(socialType = "KAKAO", token = "social-token")

        // Then
        assertEquals(ResultWrapper.Success(SignUserModel(token = null, userId = 7)), result)
    }

    // TH-1355 TC8

    @Test
    fun `TH1355_TC8_네트워크_예외는_ResultWrapper로_바꾸지_않고_호출자에게_전파된다`() {
        // Given
        val dataSource = FakeLoginDataSource(loginResult = { throw IOException("offline") })
        val repository = LoginRepositoryImpl(FakeLoginRemoteDataSource, dataSource)

        // When & Then
        assertThrows(IOException::class.java) {
            runBlocking { repository.login(socialType = "KAKAO", token = "social-token") }
        }
    }

    // TH-1355 TC9

    @Test
    fun `TH1355_TC9_푸시토큰_등록은_FCM_플랫폼으로_요청한다`() = runBlocking<Unit> {
        // Given
        val dataSource = FakeLoginDataSource(
            putPushInformationResult = { Response.success(BaseResponse(ok = true, data = "ok")) }
        )
        val repository = LoginRepositoryImpl(FakeLoginRemoteDataSource, dataSource)

        // When
        repository.registerPushToken("fcm-token")

        // Then
        assertEquals(PushInformationRequest(pushToken = "fcm-token", pushPlatformType = "FCM"), dataSource.lastPushInformationRequest)
    }

    // TH-1355 TC10

    @Test
    fun `TH1355_TC10_회원가입이_409면_GenericError_409가_그대로_전달된다`() = runBlocking<Unit> {
        // Given
        val dataSource = FakeLoginDataSource(
            signUpResult = { Response.error(409, """{"message":"이미 사용 중인 이름"}""".toResponseBody(json)) }
        )
        val repository = LoginRepositoryImpl(FakeLoginRemoteDataSource, dataSource)

        // When
        val result = repository.signUp(name = "붕어빵", socialType = "KAKAO", token = "social-token")

        // Then
        assertEquals(ResultWrapper.GenericError(409, "이미 사용 중인 이름"), result)
        assertEquals(SignUpRequest(name = "붕어빵", socialType = "KAKAO", token = "social-token"), dataSource.lastSignUpRequest)
    }
}
