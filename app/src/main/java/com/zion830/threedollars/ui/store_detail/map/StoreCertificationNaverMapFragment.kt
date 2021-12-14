package com.zion830.threedollars.ui.store_detail.map

import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.fragment.app.activityViewModels
import com.naver.maps.geometry.LatLng
import com.naver.maps.map.NaverMap
import com.naver.maps.map.overlay.CircleOverlay
import com.zion830.threedollars.R
import com.zion830.threedollars.customview.NaverMapFragment
import com.zion830.threedollars.ui.category.StoreDetailViewModel
import com.zion830.threedollars.ui.store_detail.StoreCertificationAvailableFragment.Companion.MIN_DISTANCE
import com.zion830.threedollars.utils.SizeUtils

class StoreCertificationNaverMapFragment(
    private val onMapUpdated: (LatLng?) -> Unit,
) : NaverMapFragment() {

    private val viewModel: StoreDetailViewModel by activityViewModels()

    private var circleOverlay: CircleOverlay? = null
    private var isOverlayExist = false

    override fun onMapReady(map: NaverMap) {
        super.onMapReady(map)

        viewModel.storeLocation.observe(this) {
            it?.let {
                addMarker(R.drawable.ic_marker, LatLng(it.latitude, it.longitude))
                addCircle(LatLng(it.latitude, it.longitude), MIN_DISTANCE.toDouble())
            }
        }

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
        viewModel.requestStoreInfo(viewModel.storeInfo.value?.storeId ?: 0 - 1, position.latitude, position.longitude)
        moveCamera(position)
    }
}