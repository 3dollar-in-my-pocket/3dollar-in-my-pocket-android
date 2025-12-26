package com.zion830.threedollars.ui.map.ui

import androidx.core.view.isVisible
import com.naver.maps.geometry.LatLng
import com.naver.maps.map.NaverMap
import com.zion830.threedollars.utils.SizeUtils
import dagger.hilt.android.AndroidEntryPoint
import com.zion830.threedollars.core.designsystem.R as DesignSystemR

@AndroidEntryPoint
class StoreDetailNaverMapFragment : NaverMapFragment() {

    override fun onMapReady(map: NaverMap) {
        super.onMapReady(map)
        binding.btnFindLocation.isVisible = false
        map.uiSettings.setLogoMargin(0, SizeUtils.dpToPx(300f), 0, 0) // 로고 가려지도록
    }

    fun initMap(latLng: LatLng, isClosed: Boolean? = null) {
        moveCamera(latLng)
        clearMarker()
        addMarker(if (isClosed == true) DesignSystemR.drawable.ic_mappin_focused_off else DesignSystemR.drawable.ic_mappin_focused_on, latLng)
    }
}