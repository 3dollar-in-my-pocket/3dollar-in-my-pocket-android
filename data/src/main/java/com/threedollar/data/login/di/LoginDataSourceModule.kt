package com.threedollar.data.login.di

import com.threedollar.data.login.datasource.KakaoLoginDataSource
import com.threedollar.data.login.datasource.KakaoLoginDataSourceImpl
import com.threedollar.data.login.datasource.LoginDataSource
import com.threedollar.data.login.datasource.LoginDataSourceImpl
import com.threedollar.data.login.datasource.LoginRemoteDataSource
import com.threedollar.data.login.datasource.LoginRemoteDataSourceImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
internal abstract class LoginDataSourceModule {

    @Singleton
    @Binds
    abstract fun bindLoginRemoteDataSource(impl: LoginRemoteDataSourceImpl): LoginRemoteDataSource

    @Singleton
    @Binds
    abstract fun bindLoginDataSource(impl: LoginDataSourceImpl): LoginDataSource

    @Singleton
    @Binds
    abstract fun bindKakaoLoginDataSource(impl: KakaoLoginDataSourceImpl): KakaoLoginDataSource
}
