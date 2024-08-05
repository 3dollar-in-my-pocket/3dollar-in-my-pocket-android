package com.zion830.threedollars.di

import com.threedollar.common.listener.ActivityStarter
import com.threedollar.common.listener.EventTrackerListener
import com.zion830.threedollars.ActivityStarterImpl
import com.zion830.threedollars.EventTrackerImpl
import com.zion830.threedollars.datasource.KakaoLoginDataSource
import com.zion830.threedollars.datasource.KakaoLoginDataSourceImpl
import com.zion830.threedollars.datasource.MapDataSource
import com.zion830.threedollars.datasource.MapDataSourceImpl
import com.zion830.threedollars.datasource.StoreDataSource
import com.zion830.threedollars.datasource.StoreDataSourceImpl
import com.zion830.threedollars.datasource.UserDataSource
import com.zion830.threedollars.datasource.UserDataSourceImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
abstract class SourceModule {

    @Singleton
    @Binds
    abstract fun bindMapDataSource(impl: MapDataSourceImpl): MapDataSource

    @Singleton
    @Binds
    abstract fun bindStoreDataSource(impl: StoreDataSourceImpl): StoreDataSource

    @Singleton
    @Binds
    abstract fun bindUserDataSource(impl: UserDataSourceImpl): UserDataSource

    @Singleton
    @Binds
    abstract fun bindKakaoLoginDataSource(impl: KakaoLoginDataSourceImpl): KakaoLoginDataSource

    @Singleton
    @Binds
    abstract fun bindActivityStarter(impl: ActivityStarterImpl): ActivityStarter

    @Singleton
    @Binds
    abstract fun bindEventTrackerListener(impl: EventTrackerImpl): EventTrackerListener
}