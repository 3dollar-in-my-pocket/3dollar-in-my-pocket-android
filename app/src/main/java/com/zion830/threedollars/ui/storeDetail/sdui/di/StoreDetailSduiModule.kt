package com.zion830.threedollars.ui.storeDetail.sdui.di

import com.zion830.threedollars.ui.storeDetail.sdui.model.DefaultStoreDetailSduiLogger
import com.zion830.threedollars.ui.storeDetail.sdui.model.StoreDetailSduiLogger
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
interface StoreDetailSduiModule {
    @Binds
    fun bindStoreDetailSduiLogger(impl: DefaultStoreDetailSduiLogger): StoreDetailSduiLogger
}
