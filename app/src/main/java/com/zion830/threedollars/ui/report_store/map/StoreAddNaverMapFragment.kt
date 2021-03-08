package com.zion830.threedollars.ui.report_store.map

import android.view.ViewGroup
import com.naver.maps.map.NaverMap
import com.zion830.threedollars.utils.OnMapTouchListener
import com.zion830.threedollars.utils.SizeUtils

class StoreAddNaverMapFragment(
    listener: OnMapTouchListener
) : StoreEditNaverMapFragment(listener) {

    override fun onMapReady(map: NaverMap) {
        super.onMapReady(map)

        val params = binding.btnFindLocation.layoutParams as ViewGroup.MarginLayoutParams
        params.setMargins(0, 0, SizeUtils.dpToPx(24f), SizeUtils.dpToPx(24f))
        binding.btnFindLocation.layoutParams = params

        map.uiSettings.setLogoMargin(SizeUtils.dpToPx(10f), 0, 0, SizeUtils.dpToPx(10f))
    }
}