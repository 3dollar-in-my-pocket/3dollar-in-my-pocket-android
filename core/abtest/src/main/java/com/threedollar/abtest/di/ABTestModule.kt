package com.threedollar.abtest.di

import com.threedollar.abtest.ABTestCenter
import com.threedollar.abtest.ABTestCenterImpl
import com.threedollar.abtest.source.ABTestDataSource
import com.threedollar.abtest.source.ABTestDataSourceImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
internal interface ABTestModule {

    @Singleton
    @Binds
    fun bindsABTestDataSource(
        impl: ABTestDataSourceImpl
    ): ABTestDataSource

    @Singleton
    @Binds
    fun bindsABTestCenter(
        impl: ABTestCenterImpl
    ): ABTestCenter
}
