package com.zion830.threedollars.ui.report_store.map

import android.annotation.SuppressLint
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import com.naver.maps.geometry.LatLng
import com.naver.maps.map.NaverMap
import com.zion830.threedollars.customview.NaverMapFragment
import com.zion830.threedollars.ui.addstore.AddStoreViewModel
import com.zion830.threedollars.utils.SizeUtils

class StoreAddNaverMapFragment(
    private val onMapUpdated: (LatLng?) -> Unit,
) : NaverMapFragment() {

    private val addStoreViewModel: AddStoreViewModel by activityViewModels()

    @SuppressLint("ClickableViewAccessibility")
    override fun onMapReady(map: NaverMap) {
        super.onMapReady(map)

        naverMap?.addOnCameraIdleListener {
            val selectedPosition = naverMap?.cameraPosition?.target
            onMapUpdated(selectedPosition)
            addStoreViewModel.updateLocation(selectedPosition)
        }

        val params = binding.btnFindLocation.layoutParams as ViewGroup.MarginLayoutParams
        params.setMargins(0, 0, SizeUtils.dpToPx(24f), SizeUtils.dpToPx(58f))
        binding.btnFindLocation.layoutParams = params

        moveToCurrentLocation()
    }

    override fun onMyLocationLoaded(position: LatLng) {
        super.onMyLocationLoaded(position)
        onMapUpdated(position)
        addStoreViewModel.updateLocation(position)
    }
}