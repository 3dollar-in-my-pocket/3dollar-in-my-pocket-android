package com.threedollar.data.home.di

import com.threedollar.data.home.datasource.HomeRemoteDataSource
import com.threedollar.data.home.datasource.HomeRemoteDataSourceImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
abstract class HomeDataSourceModule {

    @Singleton
    @Binds
    abstract fun bindHomeRemoteDataSource(impl: HomeRemoteDataSourceImpl): HomeRemoteDataSource
}