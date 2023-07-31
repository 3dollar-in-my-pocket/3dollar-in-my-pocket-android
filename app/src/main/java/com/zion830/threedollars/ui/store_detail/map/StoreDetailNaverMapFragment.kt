package com.zion830.threedollars.ui.store_detail.map

import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.activityViewModels
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.naver.maps.geometry.LatLng
import com.naver.maps.map.NaverMap
import com.zion830.threedollars.R
import com.zion830.threedollars.ui.category.StoreDetailViewModel
import com.zion830.threedollars.ui.map.NaverMapFragment
import com.zion830.threedollars.utils.SizeUtils
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class StoreDetailNaverMapFragment : NaverMapFragment() {
    val viewModel: StoreDetailViewModel by activityViewModels()

    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient

    override fun onMapReady(map: NaverMap) {
        super.onMapReady(map)
        fusedLocationProviderClient =
            LocationServices.getFusedLocationProviderClient(requireActivity())

        val params = binding.btnFindLocation.layoutParams as ViewGroup.MarginLayoutParams
        params.setMargins(0, 0, SizeUtils.dpToPx(24f), SizeUtils.dpToPx(55f))
        binding.btnFindLocation.layoutParams = params

        viewModel.storeLocation.observe(this) {
            it?.let {
                moveCamera(LatLng(it.latitude, it.longitude))
                addMarker(R.drawable.ic_store_selected, LatLng(it.latitude, it.longitude))
            }
        }

        binding.btnFindLocation.isVisible = false
        map.uiSettings.setLogoMargin(SizeUtils.dpToPx(30f), 0, 0, 0) // 로고 가려지도록
    }

    override fun onMyLocationLoaded(position: LatLng) {
        viewModel.requestStoreInfo(
            viewModel.storeInfo.value?.storeId ?: 0 - 1,
            position.latitude,
            position.longitude
        )
    }

    fun updateCurrentLocation(onMyLocationLoaded: (LatLng?) -> Unit) {
        updateMyLatestLocation(onMyLocationLoaded)
    }
}