package com.threedollar.data.community.di

import com.threedollar.data.community.datasource.CommunityDataSource
import com.threedollar.data.community.datasource.CommunityDataSourceImpl
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