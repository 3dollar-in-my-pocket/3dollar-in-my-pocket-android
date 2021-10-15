package com.zion830.threedollars.network

import com.zion830.threedollars.BuildConfig
import com.zion830.threedollars.utils.SharedPrefUtils
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object RetrofitBuilder {
    private const val KAKAO_SEARCH_URL = "https://dapi.kakao.com/"
    private const val KAKAO_LOGIN_URL = "https://kauth.kakao.com/"
    private val BASE_URL: String =
        if (BuildConfig.BUILD_TYPE == "debug") {
            "https://dev.threedollars.co.kr/"
        } else {
            BuildConfig.BASE_URL
        }
    private const val TIME_OUT_SEC = 5L

    private val interceptor = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }

    private val okHttpClient = OkHttpClient.Builder()
        .addInterceptor(interceptor)
        .addInterceptor {
            val request = it.request().newBuilder()
                .addHeader("Authorization", SharedPrefUtils.getAccessToken() ?: "")
                .build()
            it.proceed(request)
        }
        .connectTimeout(TIME_OUT_SEC, TimeUnit.SECONDS)
        .build()

    private val okHttpClientNoHeader = OkHttpClient.Builder()
        .addInterceptor(interceptor)
        .connectTimeout(TIME_OUT_SEC, TimeUnit.SECONDS)
        .build()

    val mapService: KakaoMapApi = Retrofit.Builder()
        .baseUrl(KAKAO_SEARCH_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .client(okHttpClientNoHeader)
        .build()
        .create(KakaoMapApi::class.java)

    val newServiceApi: NewServiceApi = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .client(okHttpClient)
        .build()
        .create(NewServiceApi::class.java)

    val kakaoLoginApi: KakaoLoginApi = Retrofit.Builder()
        .baseUrl(KAKAO_LOGIN_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .build()
        .create(KakaoLoginApi::class.java)
}