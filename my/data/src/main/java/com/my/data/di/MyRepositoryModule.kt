package com.my.data.di

import com.my.data.repository.MyRepositoryImpl
import com.my.domain.repository.MyRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
abstract class MyRepositoryModule {

    @Binds
    @Singleton
    abstract fun bindMyRepository(impl: MyRepositoryImpl): MyRepository
}