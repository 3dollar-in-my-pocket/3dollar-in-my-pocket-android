package com.zion830.threedollars.ui.store_detail.map

import androidx.core.view.isVisible
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.naver.maps.geometry.LatLng
import com.naver.maps.map.NaverMap
import com.zion830.threedollars.R
import com.zion830.threedollars.ui.map.NaverMapFragment
import com.zion830.threedollars.utils.SizeUtils
import dagger.hilt.android.AndroidEntryPoint

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
        addMarker(if (isClosed == true) R.drawable.ic_mappin_focused_off else R.drawable.ic_mappin_focused_on, latLng)
    }
}