package com.threedollar.common.di

import android.content.Context
import com.threedollar.common.utils.SharedPrefUtils
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object PrefModule {

    @Singleton
    @Provides
    fun providePrefUtil(@ApplicationContext context: Context): SharedPrefUtils {
        return SharedPrefUtils(context)
    }
}