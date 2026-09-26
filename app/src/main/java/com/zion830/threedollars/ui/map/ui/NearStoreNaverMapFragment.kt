package com.zion830.threedollars.ui.map.ui

import android.content.Intent
import androidx.core.view.isVisible
import androidx.fragment.app.activityViewModels
import com.naver.maps.geometry.LatLng
import com.naver.maps.map.CameraUpdate.REASON_GESTURE
import com.naver.maps.map.LocationTrackingMode
import com.naver.maps.map.NaverMap
import com.naver.maps.map.util.FusedLocationSource
import com.threedollar.common.utils.Constants
import com.zion830.threedollars.ui.home.viewModel.HomeViewModel
import com.zion830.threedollars.utils.NaverMapUtils
import com.zion830.threedollars.utils.isLocationAvailable
import dagger.hilt.android.AndroidEntryPoint
import com.zion830.threedollars.core.designsystem.R as DesignSystemR

@AndroidEntryPoint
class NearStoreNaverMapFragment(
    private val cameraMoved: () -> Unit = {},
) : NaverMapFragment() {
    val viewModel: HomeViewModel by activityViewModels()

    private var isFirstLoad = true

    override fun onMapReady(map: NaverMap) {
        setIsShowOverlay(isLocationAvailable())
        super.onMapReady(map)

        if (!isLocationAvailable()) {
            map.locationTrackingMode = LocationTrackingMode.None
        }

        // 홈의 현재 위치 버튼은 HomeFragment 의 지도 컨트롤(HOME_MAP_CONTROL)이 그린다.
        binding.btnFindLocation.isVisible = false

        map.addOnCameraChangeListener { reason, _ ->
            if (reason == REASON_GESTURE) {
                cameraMoved()
            }
        }
        if (isFirstLoad) {
            val savedPosition = viewModel.getSavedMapPosition()
            when {
                savedPosition != null -> moveCamera(savedPosition)
                isLocationAvailable() -> moveToCurrentLocation()
                else -> moveCamera(getCachedUserLocation() ?: NaverMapUtils.DEFAULT_LOCATION)
            }
            isFirstLoad = false
        }
    }

    fun enableLocationTracking() {
        if (naverMap?.locationSource == null) {
            naverMap?.locationSource = FusedLocationSource(this, NaverMapUtils.LOCATION_PERMISSION_REQUEST_CODE)
        }
        naverMap?.locationTrackingMode = LocationTrackingMode.NoFollow
        naverMap?.locationOverlay?.isVisible = true
        currentPosition.value?.let {
            naverMap?.locationOverlay?.position = it
        }
    }

    override fun onMyLocationLoaded(position: LatLng) {
        viewModel.updateUserLocation(position)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            Constants.GET_LOCATION_PERMISSION -> {
                moveToCurrentLocation()
            }
            Constants.MODE_ROAD_FOOD -> {
                binding.btnFindLocation.setImageResource(DesignSystemR.drawable.ic_search)
            }
        }
    }
}
