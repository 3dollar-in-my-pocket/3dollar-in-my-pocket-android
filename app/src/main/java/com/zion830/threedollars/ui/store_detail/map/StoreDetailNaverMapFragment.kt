package com.zion830.threedollars.ui.store_detail.map

import androidx.fragment.app.activityViewModels
import androidx.lifecycle.observe
import com.naver.maps.geometry.LatLng
import com.naver.maps.map.NaverMap
import com.zion830.threedollars.R
import com.zion830.threedollars.customview.NaverMapFragment
import com.zion830.threedollars.ui.store_detail.vm.StoreDetailViewModel
import com.zion830.threedollars.utils.SizeUtils

class StoreDetailNaverMapFragment : NaverMapFragment() {
    val viewModel: StoreDetailViewModel by activityViewModels()

    override fun onMapReady(map: NaverMap) {
        super.onMapReady(map)

        viewModel.storeLocation.observe(this) {
            it?.let {
                moveCamera(LatLng(it.latitude, it.longitude))
                addMarker(R.drawable.ic_store_selected, LatLng(it.latitude, it.longitude))
            }
        }
        map.uiSettings.setLogoMargin(SizeUtils.dpToPx(30f), 0, 0, SizeUtils.dpToPx(50f)) // 로고 가려지도록
    }
}