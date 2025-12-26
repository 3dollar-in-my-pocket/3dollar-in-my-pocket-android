package com.zion830.threedollars.di

import com.threedollar.common.listener.ActivityStarter
import com.threedollar.common.listener.MyFragmentStarter
import com.zion830.threedollars.ActivityStarterImpl
import com.zion830.threedollars.MyFragmentsStarterImpl
import com.zion830.threedollars.datasource.*
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
    abstract fun bindAppStatusDataSource(impl: AppStatusDataSourceImpl): AppStatusDataSource

    @Singleton
    @Binds
    abstract fun bindAppStatusRepository(impl: AppStatusRepositoryImpl): AppStatusRepository

    @Singleton
    @Binds
    abstract fun bindActivityStarter(impl: ActivityStarterImpl): ActivityStarter

    @Singleton
    @Binds
    abstract fun bindMyFragmentsStarter(impl: MyFragmentsStarterImpl): MyFragmentStarter
}