package com.threedollar.data.di

import com.threedollar.data.repository.CommunityRepositoryImpl
import com.threedollar.domain.repository.CommunityRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
abstract class CommunityRepositoryModule {

    @Binds
    @Singleton
    abstract fun bindCommunityRepository(impl: CommunityRepositoryImpl): CommunityRepository
}