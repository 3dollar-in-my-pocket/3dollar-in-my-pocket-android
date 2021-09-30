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
    private val BASE_URL: String =
        if (BuildConfig.BUILD_TYPE == "debug") {
            if (SharedPrefUtils.isTestServer()) {
                "http://3.34.255.158:8080/"
            } else {
                "https://server.3dollars-in-my-pocket.com/"
            }
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

    val service: ServiceApi = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .client(okHttpClient)
        .build()
        .create(ServiceApi::class.java)

    val mapService: KakaoMapApi = Retrofit.Builder()
        .baseUrl(KAKAO_SEARCH_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .client(okHttpClientNoHeader)
        .build()
        .create(KakaoMapApi::class.java)

    val loginService: ServiceApi = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .client(okHttpClientNoHeader)
        .build()
        .create(ServiceApi::class.java)
}