package com.threedollar.data.my.di

import com.threedollar.data.my.datasource.MyFeedbacksDataSourceImpl
import com.threedollar.data.my.datasource.MyReviewDataSourceImpl
import com.threedollar.data.my.datasource.MyStoreDataSourceImpl
import com.threedollar.data.my.datasource.MyVisitHistoryDataSourceImpl
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