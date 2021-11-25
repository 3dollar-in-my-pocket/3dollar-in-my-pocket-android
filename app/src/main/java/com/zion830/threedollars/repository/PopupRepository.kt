package com.zion830.threedollars.repository

import com.zion830.threedollars.network.NewServiceApi
import com.zion830.threedollars.network.RetrofitBuilder

class PopupRepository(
    private val newService: NewServiceApi = RetrofitBuilder.newServiceApi,
) {
    suspend fun getPopups() = newService.getPopups()
}