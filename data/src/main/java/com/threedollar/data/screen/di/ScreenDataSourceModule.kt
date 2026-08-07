package com.threedollar.data.screen.di

import com.threedollar.data.screen.datasource.ScreenRemoteDataSource
import com.threedollar.data.screen.datasource.ScreenRemoteDataSourceImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
abstract class ScreenDataSourceModule {
    @Binds
    @Singleton
    abstract fun bindScreenRemoteDataSource(impl: ScreenRemoteDataSourceImpl): ScreenRemoteDataSource
}
