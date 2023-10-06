package com.zion830.threedollars.ui.report_store.map

import android.annotation.SuppressLint
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import com.naver.maps.geometry.LatLng
import com.naver.maps.map.NaverMap
import com.zion830.threedollars.ui.category.StoreDetailViewModel
import com.zion830.threedollars.ui.map.NaverMapFragment
import com.zion830.threedollars.utils.SizeUtils
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
open class StoreEditNaverMapFragment(private val onMapUpdated: (LatLng?) -> Unit) : NaverMapFragment() {

    private var map: NaverMap? = null

    val viewModel: StoreDetailViewModel by activityViewModels()

    var currentTarget: LatLng? = null
        private set
        get() {
            return map?.cameraPosition?.target
        }


    @SuppressLint("ClickableViewAccessibility")
    override fun onMapReady(map: NaverMap) {
        super.onMapReady(map)

        naverMap?.addOnCameraIdleListener {
            val selectedPosition = naverMap?.cameraPosition?.target
            onMapUpdated(selectedPosition)
            viewModel.updateLocation(selectedPosition)
        }

        val params = binding.btnFindLocation.layoutParams as ViewGroup.MarginLayoutParams
        params.setMargins(0, 0, SizeUtils.dpToPx(24f), SizeUtils.dpToPx(58f))
        binding.btnFindLocation.layoutParams = params

//        viewModel.storeLocation.value?.let { moveCamera(it) }
    }

    override fun onMyLocationLoaded(position: LatLng) {
        super.onMyLocationLoaded(position)
        onMapUpdated(position)
        viewModel.updateLocation(position)
    }
}