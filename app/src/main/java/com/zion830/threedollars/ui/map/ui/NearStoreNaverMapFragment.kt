package com.zion830.threedollars.ui.map.ui

import android.content.Intent
import android.view.ViewGroup.MarginLayoutParams
import androidx.fragment.app.activityViewModels
import com.naver.maps.geometry.LatLng
import com.naver.maps.map.CameraUpdate.REASON_GESTURE
import com.naver.maps.map.LocationTrackingMode
import com.naver.maps.map.NaverMap
import com.naver.maps.map.util.FusedLocationSource
import com.threedollar.common.utils.Constants
import com.zion830.threedollars.ui.home.ui.HomeSheetLayout
import com.zion830.threedollars.ui.home.viewModel.HomeViewModel
import com.zion830.threedollars.utils.NaverMapUtils
import com.zion830.threedollars.utils.SizeUtils
import com.zion830.threedollars.utils.isLocationAvailable
import dagger.hilt.android.AndroidEntryPoint
import com.zion830.threedollars.core.designsystem.R as DesignSystemR

@AndroidEntryPoint
class NearStoreNaverMapFragment(
    private val cameraMoved: () -> Unit = {},
    private val onLocationButtonClicked: () -> Unit = {}
) : NaverMapFragment() {
    val viewModel: HomeViewModel by activityViewModels()

    private var isFirstLoad = true

    override fun onMapReady(map: NaverMap) {
        setIsShowOverlay(isLocationAvailable())
        super.onMapReady(map)

        if (!isLocationAvailable()) {
            map.locationTrackingMode = LocationTrackingMode.None
        }

        val params = binding.btnFindLocation.layoutParams as MarginLayoutParams
        params.setMargins(0, 0, 0, SizeUtils.dpToPx(HomeSheetLayout.LOCATION_BUTTON_BOTTOM_MARGIN_DP))
        binding.btnFindLocation.layoutParams = params

        binding.btnFindLocation.setOnClickListener {
            onLocationButtonClicked()
        }

        map.addOnCameraChangeListener { reason, _ ->
            if (reason == REASON_GESTURE) {
                cameraMoved()
            }
        }
        if (isFirstLoad) {
            val savedPosition = viewModel.getSavedMapPosition()
            if (savedPosition != null) {
                moveCamera(savedPosition)
            } else {
                if (isLocationAvailable()) {
                    moveToCurrentLocation()
                }
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
