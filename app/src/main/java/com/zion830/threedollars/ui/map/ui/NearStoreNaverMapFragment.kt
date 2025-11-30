package com.zion830.threedollars.ui.map.ui

import android.content.Intent
import android.view.ViewGroup.MarginLayoutParams
import androidx.fragment.app.activityViewModels
import com.naver.maps.geometry.LatLng
import com.naver.maps.map.CameraUpdate.REASON_GESTURE
import com.naver.maps.map.NaverMap
import com.threedollar.common.utils.Constants
import com.zion830.threedollars.R
import com.zion830.threedollars.ui.home.viewModel.HomeViewModel
import com.zion830.threedollars.utils.SizeUtils
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class NearStoreNaverMapFragment(
    private val cameraMoved: () -> Unit = {},
    private val onLocationButtonClicked: () -> Unit = {}
) : NaverMapFragment() {
    val viewModel: HomeViewModel by activityViewModels()

    private var isFirstLoad = true

    override fun onMapReady(map: NaverMap) {
        super.onMapReady(map)

        val params = binding.btnFindLocation.layoutParams as MarginLayoutParams
        params.setMargins(0, 0, 0, SizeUtils.dpToPx(178f))
        binding.btnFindLocation.layoutParams = params

        // 내 위치 버튼 클릭 리스너를 HomeFragment의 로직으로 교체
        binding.btnFindLocation.setOnClickListener {
            onLocationButtonClicked()
        }

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
        viewModel.updateUserLocation(position)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            Constants.GET_LOCATION_PERMISSION -> {
                moveToCurrentLocation()
            }
            Constants.MODE_ROAD_FOOD -> {
                binding.btnFindLocation.setImageResource(R.drawable.ic_search)
            }
        }
    }
}