package com.login.data.di

import com.login.data.datasource.KakaoLoginDataSource
import com.login.data.datasource.KakaoLoginDataSourceImpl
import com.login.data.datasource.LoginDataSource
import com.login.data.datasource.LoginDataSourceImpl
import com.login.data.datasource.LoginRemoteDataSource
import com.login.data.datasource.LoginRemoteDataSourceImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
abstract class LoginDataSourceModule {

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