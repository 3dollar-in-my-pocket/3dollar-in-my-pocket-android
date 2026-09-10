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
class NearStoreNaverMapFragment : NaverMapFragment() {
    val viewModel: HomeViewModel by activityViewModels()

    private var locationButtonBottomMarginPx = SizeUtils.dpToPx(HomeSheetLayout.LOCATION_BUTTON_BOTTOM_MARGIN_DP)
    private var cameraMoved: () -> Unit = {}
    private var onLocationButtonClicked: () -> Unit = {}
    private var mapReady: () -> Unit = {}

    fun attachCallbacks(
        cameraMoved: () -> Unit,
        onLocationButtonClicked: () -> Unit,
        onMapReady: () -> Unit,
    ) {
        this.cameraMoved = cameraMoved
        this.onLocationButtonClicked = onLocationButtonClicked
        this.mapReady = onMapReady
        if (naverMap != null) onMapReady()
    }

    override fun onMapReady(map: NaverMap) {
        setIsShowOverlay(isLocationAvailable())
        super.onMapReady(map)

        map.locationTrackingMode = if (isLocationAvailable()) {
            LocationTrackingMode.NoFollow
        } else {
            LocationTrackingMode.None
        }

        applyLocationButtonBottomMargin()

        binding.btnFindLocation.setOnClickListener {
            onLocationButtonClicked()
        }

        map.addOnCameraChangeListener { reason, _ ->
            if (reason == REASON_GESTURE) {
                cameraMoved()
            }
        }
        mapReady()
    }

    fun updateLocationButtonBottomMargin(bottomMarginPx: Int) {
        locationButtonBottomMarginPx = bottomMarginPx
        if (view != null) {
            applyLocationButtonBottomMargin()
        }
    }

    private fun applyLocationButtonBottomMargin() {
        val params = binding.btnFindLocation.layoutParams as MarginLayoutParams
        if (params.bottomMargin == locationButtonBottomMarginPx) return

        params.bottomMargin = locationButtonBottomMarginPx
        binding.btnFindLocation.layoutParams = params
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
