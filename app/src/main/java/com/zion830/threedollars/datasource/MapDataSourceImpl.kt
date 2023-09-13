package com.zion830.threedollars.datasource

import com.naver.maps.geometry.LatLng
import com.threedollar.network.api.KakaoMapApi
import com.threedollar.network.data.kakao.SearchAddressResponse
import javax.inject.Inject

class MapDataSourceImpl @Inject constructor(private val service: KakaoMapApi) : MapDataSource {

    override suspend fun searchAddress(query: String, latLng: LatLng): SearchAddressResponse =
        service.searchAddress(query, latLng.longitude, latLng.latitude)
}