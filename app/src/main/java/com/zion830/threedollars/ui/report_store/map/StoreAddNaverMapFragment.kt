package com.zion830.threedollars.ui.report_store.map

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import com.naver.maps.geometry.LatLng
import com.naver.maps.map.NaverMap
import com.zion830.threedollars.customview.NaverMapFragment
import com.zion830.threedollars.ui.addstore.AddStoreViewModel
import com.zion830.threedollars.ui.addstore.activity.NewStoreActivity
import com.zion830.threedollars.utils.SizeUtils

class StoreAddNaverMapFragment(
    private val onMapUpdated: (LatLng?) -> Unit,
) : NaverMapFragment() {

    private val addStoreViewModel: AddStoreViewModel by activityViewModels()

    private var isIdleAvailable = false

    private var lastTime: Long = 0

    @SuppressLint("ClickableViewAccessibility")
    override fun onMapReady(map: NaverMap) {
        super.onMapReady(map)

        naverMap?.addOnCameraIdleListener {
            if (System.currentTimeMillis() - lastTime > 1000 && isIdleAvailable) {
                val selectedPosition = naverMap?.cameraPosition?.target
                onMapUpdated(selectedPosition)
                addStoreViewModel.updateLocation(selectedPosition)
                lastTime = System.currentTimeMillis()
            }
        }

        val params = binding.btnFindLocation.layoutParams as ViewGroup.MarginLayoutParams
        params.setMargins(0, 0, SizeUtils.dpToPx(24f), SizeUtils.dpToPx(58f))
        binding.btnFindLocation.layoutParams = params

        val latitude = arguments?.getDouble(NewStoreActivity.KEY_LATITUDE) ?: -1.0
        val longitude = arguments?.getDouble(NewStoreActivity.KEY_LONGITUDE) ?: -1.0

        moveToCurrentLocation(false)
    }

    override fun onMyLocationLoaded(position: LatLng) {
        super.onMyLocationLoaded(position)
        onMapUpdated(position)
        addStoreViewModel.updateLocation(position)
        isIdleAvailable = true
    }

    companion object {
        fun getInstance(
            latLng: LatLng?,
            onMapUpdated: (LatLng?) -> Unit,
        ) = StoreAddNaverMapFragment(onMapUpdated).apply {
            latLng?.let {
                val bundle = Bundle()
                bundle.putDouble(NewStoreActivity.KEY_LATITUDE, latLng.latitude)
                bundle.putDouble(NewStoreActivity.KEY_LONGITUDE, latLng.longitude)
                arguments = bundle
            }
        }
    }
}