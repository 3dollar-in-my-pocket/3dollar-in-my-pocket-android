package com.threedollar.data.di

import com.threedollar.data.datasource.CommunityDataSource
import com.threedollar.data.datasource.CommunityDataSourceImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
abstract class CommunityDataSourceModule {

    @Binds
    @Singleton
    abstract fun bindCommunityDataSource(impl: CommunityDataSourceImpl): CommunityDataSource
}