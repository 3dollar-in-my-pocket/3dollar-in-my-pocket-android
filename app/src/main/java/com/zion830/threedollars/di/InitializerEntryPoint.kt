package com.zion830.threedollars.di

import android.content.Context
import com.threedollar.abtest.ABTestCenter
import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.android.EntryPointAccessors
import dagger.hilt.components.SingletonComponent

@EntryPoint
@InstallIn(SingletonComponent::class)
interface InitializerEntryPoint {

    fun abTestCenter(): ABTestCenter

    companion object {
        fun resolve(
            context: Context
        ): InitializerEntryPoint = EntryPointAccessors.fromApplication(context, InitializerEntryPoint::class.java)
    }
}
