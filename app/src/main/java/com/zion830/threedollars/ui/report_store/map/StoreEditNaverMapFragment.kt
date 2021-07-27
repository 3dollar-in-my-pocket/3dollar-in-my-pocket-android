package com.zion830.threedollars.ui.report_store.map

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.observe
import com.naver.maps.geometry.LatLng
import com.naver.maps.map.NaverMap
import com.zion830.threedollars.R
import com.zion830.threedollars.customview.NaverMapFragment
import com.zion830.threedollars.ui.store_detail.vm.StoreDetailViewModel
import com.zion830.threedollars.utils.OnMapTouchListener
import com.zion830.threedollars.utils.SizeUtils
import com.zion830.threedollars.utils.TouchableWrapper

open class StoreEditNaverMapFragment(
    private val onMapUpdated: (LatLng?) -> Unit,
    val listener: OnMapTouchListener? = null,
) : NaverMapFragment() {

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

        viewModel.storeLocation.value?.let { moveCamera(it) }
    }

    override fun onMyLocationLoaded(position: LatLng) {
        super.onMyLocationLoaded(position)
        onMapUpdated(position)
        viewModel.updateLocation(position)
    }
}