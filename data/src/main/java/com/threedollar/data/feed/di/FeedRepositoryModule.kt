package com.threedollar.data.feed.di

import com.threedollar.data.feed.repository.FeedRepositoryImpl
import com.threedollar.domain.feed.repository.FeedRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.android.scopes.ViewModelScoped

@InstallIn(ViewModelComponent::class)
@Module
interface FeedRepositoryModule {

    @Binds
    @ViewModelScoped
    fun bindFeedRepository(impl: FeedRepositoryImpl): FeedRepository
}
