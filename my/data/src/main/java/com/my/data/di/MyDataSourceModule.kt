package com.my.data.di

import com.my.data.datasource.MyFeedbacksDataSourceImpl
import com.my.data.datasource.MyReviewDataSourceImpl
import com.my.data.datasource.MyStoreDataSourceImpl
import com.my.data.datasource.MyVisitHistoryDataSourceImpl
import com.threedollar.network.api.ServerApi
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
object MyDataSourceModule {

    @Provides
    @Singleton
    fun provideMyStoreDataSource(serverApi: ServerApi): MyStoreDataSourceImpl {
        return MyStoreDataSourceImpl(serverApi)
    }

    @Provides
    @Singleton
    fun provideMyReviewDataSource(serverApi: ServerApi): MyReviewDataSourceImpl {
        return MyReviewDataSourceImpl(serverApi)
    }

    @Provides
    @Singleton
    fun provideMyFeedbacksDataSource(serverApi: ServerApi): MyFeedbacksDataSourceImpl {
        return MyFeedbacksDataSourceImpl(serverApi)
    }

    @Provides
    @Singleton
    fun provideMyVisitHistoryDataSource(serverApi: ServerApi): MyVisitHistoryDataSourceImpl {
        return MyVisitHistoryDataSourceImpl(serverApi)
    }
}