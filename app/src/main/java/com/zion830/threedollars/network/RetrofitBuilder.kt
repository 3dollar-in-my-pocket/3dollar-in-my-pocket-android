package com.zion830.threedollars.network

import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import com.zion830.threedollars.R
import com.zion830.threedollars.utils.StringUtils
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import java.util.concurrent.TimeUnit

object RetrofitBuilder {
    private val BASE_URL = StringUtils.getString(R.string.base_url)
    private val KAKAO_LOGIN_URL = StringUtils.getString(R.string.kakao_url)
    private const val TIME_OUT_SEC = 5L

    private val interceptor = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }

    private val okHttpClient = OkHttpClient.Builder()
        .addInterceptor(interceptor)
        .connectTimeout(TIME_OUT_SEC, TimeUnit.SECONDS)
        .build()

    val service: ServiceApi = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .addConverterFactory(Json.asConverterFactory("application/json".toMediaType()))
        .client(okHttpClient)
        .build()
        .create(ServiceApi::class.java)

    val kakaoService: KakaoApi = Retrofit.Builder()
        .baseUrl(KAKAO_LOGIN_URL)
        .addConverterFactory(Json.asConverterFactory("application/json".toMediaType()))
        .client(okHttpClient)
        .build()
        .create(KakaoApi::class.java)
}