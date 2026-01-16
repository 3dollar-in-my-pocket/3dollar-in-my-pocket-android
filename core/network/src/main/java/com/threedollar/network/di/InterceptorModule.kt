package com.threedollar.network.di

import com.threedollar.network.interceptor.ABTestInterceptor
import com.threedollar.network.interceptor.ABTestInterceptorImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
internal interface InterceptorModule {

    @Singleton
    @Binds
    fun bindsABTestInterceptor(
        impl: ABTestInterceptorImpl
    ): ABTestInterceptor
}
