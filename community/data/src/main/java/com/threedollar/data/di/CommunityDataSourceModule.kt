package com.threedollar.data.di

import com.threedollar.data.datasource.CommunityRemoteDataSource
import com.threedollar.data.datasource.CommunityRemoteDataSourceImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
abstract class CommunityDataSourceModule {

    @Singleton
    @Binds
    abstract fun bindCommunityRemoteDataSource(impl: CommunityRemoteDataSourceImpl): CommunityRemoteDataSource
}