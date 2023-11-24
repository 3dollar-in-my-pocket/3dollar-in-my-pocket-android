package com.zion830.threedollars.datasource

import com.naver.maps.geometry.LatLng
import com.threedollar.network.data.kakao.SearchAddressResponse

interface MapDataSource {
    suspend fun searchAddress(query: String, latLng: LatLng): SearchAddressResponse
}