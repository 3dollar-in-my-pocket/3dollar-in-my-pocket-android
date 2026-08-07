package com.threedollar.data.screen.di

import com.threedollar.data.screen.repository.ScreenRepositoryImpl
import com.threedollar.domain.screen.repository.ScreenRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
abstract class ScreenRepositoryModule {
    @Binds
    @Singleton
    abstract fun bindScreenRepository(impl: ScreenRepositoryImpl): ScreenRepository
}
