package com.threedollar.data.login.di

import com.threedollar.data.login.repository.SignUpRepositoryImpl
import com.threedollar.domain.login.repository.SignUpRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@InstallIn(SingletonComponent::class)
@Module
internal interface RepositoryModule {

    @Binds
    fun bindsSignUpRepository(
        impl: SignUpRepositoryImpl
    ): SignUpRepository
}
