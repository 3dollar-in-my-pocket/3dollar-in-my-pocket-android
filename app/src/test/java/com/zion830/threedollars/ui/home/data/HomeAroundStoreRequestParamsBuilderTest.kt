package com.zion830.threedollars.ui.home.data

import com.naver.maps.geometry.LatLng
import org.junit.Assert.assertEquals
import org.junit.Test

class HomeAroundStoreRequestParamsBuilderTest {

    @Test
    fun build_usesResolvedInitialLocationForMapAndDeviceCoordinates() {
        val resolvedLocation = LatLng(37.5123, 127.0456)

        val params = HomeAroundStoreRequestParamsBuilder.build(
            state = HomeUIState(
                mapPosition = resolvedLocation,
                userLocation = resolvedLocation,
            ),
            bars = emptyList(),
        )

        assertEquals(resolvedLocation.latitude, params.mapLatitude, 0.0)
        assertEquals(resolvedLocation.longitude, params.mapLongitude, 0.0)
        assertEquals(resolvedLocation.latitude, params.deviceLatitude, 0.0)
        assertEquals(resolvedLocation.longitude, params.deviceLongitude, 0.0)
    }

    @Test
    fun build_keepsMapPositionAndDeviceLocationSeparate() {
        val mapPosition = LatLng(37.6001, 126.9901)
        val userLocation = LatLng(37.5002, 127.0302)

        val params = HomeAroundStoreRequestParamsBuilder.build(
            state = HomeUIState(
                mapPosition = mapPosition,
                userLocation = userLocation,
            ),
            bars = emptyList(),
        )

        assertEquals(mapPosition.latitude, params.mapLatitude, 0.0)
        assertEquals(mapPosition.longitude, params.mapLongitude, 0.0)
        assertEquals(userLocation.latitude, params.deviceLatitude, 0.0)
        assertEquals(userLocation.longitude, params.deviceLongitude, 0.0)
    }
}
