package com.zion830.threedollars.datasource

import com.naver.maps.geometry.LatLng
import com.zion830.threedollars.datasource.model.v2.response.kakao.SearchAddressResponse
import com.zion830.threedollars.network.KakaoMapApi
import javax.inject.Inject

class MapDataSourceImpl @Inject constructor(private val service: KakaoMapApi) : MapDataSource {

    override suspend fun searchAddress(query: String, latLng: LatLng): SearchAddressResponse =
        service.searchAddress(query, latLng.longitude, latLng.latitude)
}