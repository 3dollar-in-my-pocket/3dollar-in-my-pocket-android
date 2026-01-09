package com.threedollar.domain.login.di

import com.threedollar.domain.login.usecase.GetRandomNameUseCase
import com.threedollar.domain.login.usecase.SignUpUseCase
import com.threedollar.domain.login.usecase.impl.GetRandomNameUseCaseImpl
import com.threedollar.domain.login.usecase.impl.SignUpUseCaseImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityRetainedComponent
import dagger.hilt.android.scopes.ActivityRetainedScoped

@Module
@InstallIn(ActivityRetainedComponent::class)
internal interface UseCaseModule {
    @Binds
    @ActivityRetainedScoped
    fun bindsSignUpUseCase(impl: SignUpUseCaseImpl): SignUpUseCase

    @Binds
    @ActivityRetainedScoped
    fun bindsGetRandomNameUseCase(impl: GetRandomNameUseCaseImpl): GetRandomNameUseCase
}
