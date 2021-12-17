package com.zion830.threedollars.repository

import com.naver.maps.geometry.LatLng
import com.zion830.threedollars.network.KakaoMapApi
import com.zion830.threedollars.network.RetrofitBuilder
import com.zion830.threedollars.repository.model.v2.response.kakao.SearchAddressResponse

class MapRepository(
    private val service: KakaoMapApi = RetrofitBuilder.mapService
) {

    suspend fun searchAddress(query: String, latLng: LatLng): SearchAddressResponse = service.searchAddress(query, latLng.longitude, latLng.latitude)
}