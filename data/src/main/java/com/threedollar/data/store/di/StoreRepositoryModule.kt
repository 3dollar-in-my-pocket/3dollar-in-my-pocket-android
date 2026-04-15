package com.threedollar.data.store.di

import com.threedollar.data.store.repository.StoreRepositoryImpl
import com.threedollar.domain.store.repository.StoreRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.android.scopes.ViewModelScoped

@InstallIn(ViewModelComponent::class)
@Module
interface StoreRepositoryModule {

    @Binds
    @ViewModelScoped
    fun bindStoreRepository(impl: StoreRepositoryImpl): StoreRepository
}
