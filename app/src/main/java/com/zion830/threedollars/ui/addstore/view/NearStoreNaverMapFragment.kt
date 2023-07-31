package com.zion830.threedollars.ui.addstore.view

import android.content.Intent
import android.view.ViewGroup.MarginLayoutParams
import androidx.fragment.app.activityViewModels
import com.naver.maps.geometry.LatLng
import com.naver.maps.map.CameraUpdate.REASON_GESTURE
import com.naver.maps.map.NaverMap
import com.zion830.threedollars.Constants
import com.zion830.threedollars.R
import com.zion830.threedollars.ui.home.HomeViewModel
import com.zion830.threedollars.ui.map.NaverMapFragment
import com.zion830.threedollars.utils.SizeUtils
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class NearStoreNaverMapFragment(private val cameraMoved: () -> Unit = {}) : NaverMapFragment() {
    val viewModel: HomeViewModel by activityViewModels()

    private var isFirstLoad = true

    private var isRoadFoodMode = true

    override fun onMapReady(map: NaverMap) {
        super.onMapReady(map)

        val params = binding.btnFindLocation.layoutParams as MarginLayoutParams
        params.setMargins(0, 0, 0, SizeUtils.dpToPx(240f))
        binding.btnFindLocation.layoutParams = params

        map.addOnCameraChangeListener { reason, _ ->
            if (reason == REASON_GESTURE) {
                cameraMoved()
            }
        }
        if (isFirstLoad) {
            moveToCurrentLocation()
            isFirstLoad = false
        }
    }

    override fun onMyLocationLoaded(position: LatLng) {
        if (isRoadFoodMode) {
            viewModel.requestHomeItem(position)
        } else {
            viewModel.getBossNearStore(position)
        }
        viewModel.updateCurrentLocation(position)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            Constants.GET_LOCATION_PERMISSION -> {
                moveToCurrentLocation()
            }
            Constants.MODE_ROAD_FOOD -> {
                binding.btnFindLocation.setImageResource(R.drawable.ic_search)
                isRoadFoodMode = true
            }
            Constants.MODE_FOOD_TRUCK -> {
                binding.btnFindLocation.setImageResource(R.drawable.ic_search_green)
                isRoadFoodMode = false
            }
        }
    }
}