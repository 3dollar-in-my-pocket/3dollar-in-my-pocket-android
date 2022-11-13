package com.zion830.threedollars.network

import android.os.Build
import android.os.Handler
import android.os.Looper
import com.zion830.threedollars.BuildConfig
import com.zion830.threedollars.R
import com.zion830.threedollars.utils.SharedPrefUtils
import com.zion830.threedollars.utils.showToast
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit


object RetrofitBuilder {
    private const val KAKAO_SEARCH_URL = "https://dapi.kakao.com/"
    private const val KAKAO_LOGIN_URL = "https://kauth.kakao.com/"
    private val BASE_URL: String = BuildConfig.BASE_URL

    private const val TIME_OUT_SEC = 5L

    private val interceptor = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }

    private val okHttpClient = OkHttpClient.Builder()
        .addInterceptor(interceptor)
        .addInterceptor {
            val request = it.request().newBuilder()
                .removeHeader("User-Agent")
                .addHeader(
                    "User-Agent",
                    BuildConfig.VERSION_NAME + " (${BuildConfig.APPLICATION_ID}); " + Build.VERSION.SDK_INT
                )
                .addHeader(
                    "Authorization",
                    SharedPrefUtils.getAccessToken() ?: ""
                )
                .addHeader(
                    "X-ANDROID-SERVICE-VERSION",
                    BuildConfig.BUILD_TYPE + "_" + BuildConfig.VERSION_NAME
                )
                .build()

            it.proceed(request).apply {
                if (code == 429) {
                    // 추후 BaseViewModel safeApiCall로 로직 옮겨야함
                    val handler = Handler(Looper.getMainLooper())
                    handler.postDelayed(Runnable {
                        showToast(R.string.error_429)
                    }, 0)
                }
            }
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
        .client(okHttpClientNoHeader)
        .build()
        .create(KakaoLoginApi::class.java)
}