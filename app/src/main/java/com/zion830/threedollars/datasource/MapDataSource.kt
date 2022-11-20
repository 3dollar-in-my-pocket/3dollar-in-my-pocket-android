package com.zion830.threedollars.datasource

import com.naver.maps.geometry.LatLng
import com.zion830.threedollars.datasource.model.v2.response.kakao.SearchAddressResponse

interface MapDataSource {
    suspend fun searchAddress(query: String, latLng: LatLng): SearchAddressResponse
}