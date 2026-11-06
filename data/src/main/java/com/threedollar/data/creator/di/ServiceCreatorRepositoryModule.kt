package com.threedollar.data.creator.di

import com.threedollar.data.creator.repository.ServiceCreatorRepositoryImpl
import com.threedollar.domain.creator.repository.ServiceCreatorRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.android.scopes.ViewModelScoped

@InstallIn(ViewModelComponent::class)
@Module
interface ServiceCreatorRepositoryModule {

    @Binds
    @ViewModelScoped
    fun bindServiceCreatorRepository(impl: ServiceCreatorRepositoryImpl): ServiceCreatorRepository
}
