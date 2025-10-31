package com.zion830.threedollars.ui.map.ui

import android.os.Bundle
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import com.naver.maps.geometry.LatLng
import com.naver.maps.map.NaverMap
import com.naver.maps.map.overlay.CircleOverlay
import com.zion830.threedollars.R
import com.zion830.threedollars.core.designsystem.R as DesignSystemR
import com.zion830.threedollars.ui.storeDetail.user.ui.StoreCertificationAvailableFragment.Companion.MIN_DISTANCE
import com.zion830.threedollars.utils.SizeUtils
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class StoreCertificationNaverMapFragment : NaverMapFragment() {

    companion object {
        private const val ARG_LATITUDE = "ARG_LATITUDE"
        private const val ARG_LONGITUDE = "ARG_LONGITUDE"

        // newInstance 메서드를 통해 Fragment 생성 및 인자 전달
        fun newInstance(latLng: LatLng): StoreCertificationNaverMapFragment {
            return StoreCertificationNaverMapFragment().apply {
                arguments = Bundle().apply {
                    putDouble(ARG_LATITUDE, latLng.latitude)
                    putDouble(ARG_LONGITUDE, latLng.longitude)
                }
            }
        }
    }

    private var latLng: LatLng = LatLng(0.0, 0.0)
    private var circleOverlay: CircleOverlay? = null
    private var isOverlayExist = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            val latitude = it.getDouble(ARG_LATITUDE)
            val longitude = it.getDouble(ARG_LONGITUDE)
            latLng = LatLng(latitude, longitude)
        }
    }

    override fun onMapReady(map: NaverMap) {
        super.onMapReady(map)

        addMarker(DesignSystemR.drawable.ic_mappin_focused_on, latLng)
        addCircle(latLng, MIN_DISTANCE.toDouble())

        initFindLocationButton()
        map.uiSettings.setLogoMargin(SizeUtils.dpToPx(30f), 0, 0, 0) // 로고 가려지도록
    }

    private fun addCircle(latLng: LatLng, radius: Double) {
        circleOverlay = CircleOverlay(latLng, radius).apply {
            color = ContextCompat.getColor(requireContext(), DesignSystemR.color.certification_radius)
            outlineWidth = 1
            outlineColor = ContextCompat.getColor(requireContext(), DesignSystemR.color.color_sub_red)
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