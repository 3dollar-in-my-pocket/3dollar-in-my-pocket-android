package com.zion830.threedollars.ui.store_detail.map

import android.util.Log
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.activityViewModels
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.naver.maps.geometry.LatLng
import com.naver.maps.map.NaverMap
import com.zion830.threedollars.R
import com.zion830.threedollars.customview.NaverMapFragment
import com.zion830.threedollars.ui.food_truck_store_detail.FoodTruckStoreDetailViewModel
import com.zion830.threedollars.utils.SizeUtils

class FoodTruckStoreDetailNaverMapFragment : NaverMapFragment() {
    val viewModel: FoodTruckStoreDetailViewModel by activityViewModels()

    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient

    override fun onMapReady(map: NaverMap) {
        super.onMapReady(map)
        fusedLocationProviderClient =
            LocationServices.getFusedLocationProviderClient(requireActivity())

        val params = binding.btnFindLocation.layoutParams as ViewGroup.MarginLayoutParams
        params.setMargins(0, 0, SizeUtils.dpToPx(24f), SizeUtils.dpToPx(55f))
        binding.btnFindLocation.layoutParams = params

        viewModel.bossStoreDetailModel.observe(viewLifecycleOwner) {
            val location = LatLng(it.location.latitude, it.location.longitude)

            moveCamera(location)
            addMarker(R.drawable.ic_marker_green, location)
        }

        binding.btnFindLocation.isVisible = true
        binding.btnFindLocation.setImageResource(R.drawable.ic_search_green)

        map.uiSettings.setLogoMargin(SizeUtils.dpToPx(30f), 0, 0, 0) // 로고 가려지도록
    }

    override fun onMyLocationLoaded(position: LatLng) {
        viewModel.getFoodTruckStoreDetail(
            viewModel.bossStoreDetailModel.value?.bossStoreId ?: "",
            position.latitude,
            position.longitude
        )
    }

    fun updateCurrentLocation(onMyLocationLoaded: (LatLng?) -> Unit) {
        updateMyLatestLocation(onMyLocationLoaded)
    }
}