package com.zion830.threedollars.repository

import com.zion830.threedollars.network.NaverMapApi
import com.zion830.threedollars.network.RetrofitBuilder
import com.zion830.threedollars.repository.model.response.SearchAddressResponse

class MapRepository(
    private val service: NaverMapApi = RetrofitBuilder.mapService
) {

    suspend fun searchAddress(query: String): SearchAddressResponse = service.searchAddress(query = query)
}