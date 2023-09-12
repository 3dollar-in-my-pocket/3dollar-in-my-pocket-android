package com.zion830.threedollars.di

import android.os.Build
import com.threedollar.network.BuildConfig
import com.threedollar.network.api.KakaoLoginApi
import com.threedollar.network.api.KakaoMapApi
import com.threedollar.network.di.NetworkModule.getLoggerInterceptor
import com.zion830.threedollars.network.NewServiceApi
import com.zion830.threedollars.utils.LegacySharedPrefUtils
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
object LegacyNetworkModule {

        private const val BASE_URL: String = BuildConfig.BASE_URL
        private const val TIME_OUT_SEC = 5L

        @Qualifier
        @Retention(AnnotationRetention.BINARY)
        annotation class LegacyNoHeaderOkhttpClient

        @Qualifier
        @Retention(AnnotationRetention.BINARY)
        annotation class LegacyOkhttpClient

        @Singleton
        @Provides
        @LegacyOkhttpClient
        fun legacyProvideOkHttpClient(interceptor: HttpLoggingInterceptor): OkHttpClient =
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
                            LegacySharedPrefUtils.getAccessToken() ?: ""
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
        @LegacyNoHeaderOkhttpClient
        fun legacyProvideNoHeaderOkHttpClient(interceptor: HttpLoggingInterceptor): OkHttpClient =
            OkHttpClient.Builder()
                .addInterceptor(interceptor)
                .connectTimeout(TIME_OUT_SEC, TimeUnit.SECONDS)
                .build()

        @Provides
        @Singleton
        fun legacyProvideServerApiApi(@LegacyOkhttpClient okHttpClientLegacy: OkHttpClient): NewServiceApi =
            Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .client(okHttpClientLegacy)
                .build()
                .create(NewServiceApi::class.java)


        val newServiceApi: NewServiceApi = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .client(legacyProvideOkHttpClient(getLoggerInterceptor()))
            .build()
            .create(NewServiceApi::class.java)
    }