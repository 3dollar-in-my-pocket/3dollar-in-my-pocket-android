package com.threedollar.network.di

import android.os.Build
import com.threedollar.common.utils.SharedPrefUtils
import com.threedollar.network.BuildConfig
import com.threedollar.network.api.KakaoLoginApi
import com.threedollar.network.api.KakaoMapApi
import com.threedollar.network.api.ServerApi
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Qualifier
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    private const val KAKAO_SEARCH_URL = "https://dapi.kakao.com/"
    private const val KAKAO_LOGIN_URL = "https://kauth.kakao.com/"
    private const val BASE_URL: String = BuildConfig.BASE_URL
    private const val TIME_OUT_SEC = 5L

    @Qualifier
    @Retention(AnnotationRetention.BINARY)
    annotation class NoHeaderOkhttpClient

    @Qualifier
    @Retention(AnnotationRetention.BINARY)
    annotation class OkhttpClient

    @Provides
    @Singleton
    fun getLoggerInterceptor() = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }

    @Singleton
    @Provides
    @OkhttpClient
    fun provideOkHttpClient(sharedPrefUtils: SharedPrefUtils, interceptor: HttpLoggingInterceptor): OkHttpClient =
        OkHttpClient.Builder()
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
                        sharedPrefUtils.getAccessToken() ?: ""
                    )
                    .addHeader(
                        "X-ANDROID-SERVICE-VERSION",
                        BuildConfig.BUILD_TYPE + "_" + BuildConfig.VERSION_NAME
                    )
                    .build()

                it.proceed(request)
            }
            .connectTimeout(TIME_OUT_SEC, TimeUnit.SECONDS)
            .build()

    @Singleton
    @Provides
    @NoHeaderOkhttpClient
    fun provideNoHeaderOkHttpClient(interceptor: HttpLoggingInterceptor): OkHttpClient =
        OkHttpClient.Builder()
            .addInterceptor(interceptor)
            .connectTimeout(TIME_OUT_SEC, TimeUnit.SECONDS)
            .build()

    @Provides
    @Singleton
    fun provideMapService(@NoHeaderOkhttpClient okHttpClient: OkHttpClient): KakaoMapApi =
        Retrofit.Builder()
            .baseUrl(KAKAO_SEARCH_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .client(okHttpClient)
            .build()
            .create(KakaoMapApi::class.java)

    @Provides
    @Singleton
    fun provideServerApiApi(@OkhttpClient okHttpClient: OkHttpClient): ServerApi =
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .client(okHttpClient)
            .build()
            .create(ServerApi::class.java)

    @Provides
    @Singleton
    fun provideKakaoLoginApi(@NoHeaderOkhttpClient okHttpClient: OkHttpClient): KakaoLoginApi =
        Retrofit.Builder()
            .baseUrl(KAKAO_LOGIN_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .client(okHttpClient)
            .build()
            .create(KakaoLoginApi::class.java)

}
