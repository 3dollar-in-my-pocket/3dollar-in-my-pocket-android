package com.threedollar.data.store.di

import com.threedollar.data.store.repository.ThreadsRepositoryImpl
import com.threedollar.domain.store.repository.ThreadsRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.android.scopes.ViewModelScoped

@InstallIn(ViewModelComponent::class)
@Module
interface ThreadsRepositoryModule {

    @Binds
    @ViewModelScoped
    fun bindThreadsRepository(impl: ThreadsRepositoryImpl): ThreadsRepository
}
