package com.zion830.threedollars.ui.map.ui

import android.view.ViewGroup
import androidx.core.view.isVisible
import com.naver.maps.geometry.LatLng
import com.naver.maps.map.NaverMap
import com.zion830.threedollars.R
import com.zion830.threedollars.core.designsystem.R as DesignSystemR
import com.zion830.threedollars.utils.SizeUtils

class FullScreenNaverMapFragment : NaverMapFragment() {
    override fun onMapReady(map: NaverMap) {
        super.onMapReady(map)
        binding.btnFindLocation.isVisible = true
        map.uiSettings.setLogoMargin(0, SizeUtils.dpToPx(300f), SizeUtils.dpToPx(300f), 0) // 로고 가려지도록
        val params = binding.btnFindLocation.layoutParams as ViewGroup.MarginLayoutParams
        params.setMargins(0, 0, 0, SizeUtils.dpToPx(32f))
        binding.btnFindLocation.layoutParams = params
    }

    fun initMap(latLng: LatLng, isClosed: Boolean) {
        moveCamera(latLng)
        addMarker(if (isClosed) DesignSystemR.drawable.ic_mappin_focused_off else DesignSystemR.drawable.ic_mappin_focused_on, latLng)
    }
}