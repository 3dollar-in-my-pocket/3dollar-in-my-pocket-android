package com.zion830.threedollars.ui.store_detail.map

import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import com.naver.maps.geometry.LatLng
import com.naver.maps.map.NaverMap
import com.naver.maps.map.overlay.CircleOverlay
import com.zion830.threedollars.R
import com.zion830.threedollars.ui.map.NaverMapFragment
import com.zion830.threedollars.ui.store_detail.StoreCertificationAvailableFragment.Companion.MIN_DISTANCE
import com.zion830.threedollars.utils.SizeUtils
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class StoreCertificationNaverMapFragment(private val latLng: LatLng) : NaverMapFragment() {

    private var circleOverlay: CircleOverlay? = null
    private var isOverlayExist = false

    override fun onMapReady(map: NaverMap) {
        super.onMapReady(map)

        addMarker(R.drawable.ic_mappin_focused_on, latLng)
        addCircle(latLng, MIN_DISTANCE.toDouble())

        initFindLocationButton()
        map.uiSettings.setLogoMargin(SizeUtils.dpToPx(30f), 0, 0, 0) // 로고 가려지도록
    }

    private fun addCircle(latLng: LatLng, radius: Double) {
        circleOverlay = CircleOverlay(latLng, radius).apply {
            color = ContextCompat.getColor(requireContext(), R.color.certification_radius)
            outlineWidth = 1
            outlineColor = ContextCompat.getColor(requireContext(), R.color.color_sub_red)
        }

        if (!isOverlayExist) {
            circleOverlay?.map = naverMap
            isOverlayExist = true
        }
    }

    private fun initFindLocationButton() {
        val params = binding.btnFindLocation.layoutParams as ViewGroup.MarginLayoutParams
        params.setMargins(0, 0, SizeUtils.dpToPx(24f), SizeUtils.dpToPx(24f))
        binding.btnFindLocation.layoutParams = params
        binding.btnFindLocation.isVisible = true
        binding.btnFindLocation.setOnClickListener {
            moveToCurrentLocation(true)
        }
    }

    override fun onMyLocationLoaded(position: LatLng) {
//        viewModel.requestStoreInfo(
//            viewModel.storeInfo.value?.storeId ?: 0 - 1,
//            position.latitude,
//            position.longitude
//        )
        moveCamera(position)
    }
}