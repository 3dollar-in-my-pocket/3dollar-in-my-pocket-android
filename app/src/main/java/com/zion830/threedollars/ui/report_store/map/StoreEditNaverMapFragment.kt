package com.zion830.threedollars.ui.report_store.map

import android.view.View
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.observe
import com.naver.maps.geometry.LatLng
import com.naver.maps.map.NaverMap
import com.zion830.threedollars.R
import com.zion830.threedollars.customview.NaverMapFragment
import com.zion830.threedollars.ui.report_store.vm.StoreEditViewModel
import com.zion830.threedollars.utils.SizeUtils
import com.zion830.threedollars.utils.getCurrentLocationName

open class StoreEditNaverMapFragment : NaverMapFragment() {

    private var map: NaverMap? = null

    val viewModel: StoreEditViewModel by activityViewModels()

    var currentTarget: LatLng? = null
        private set
        get() {
            return map?.cameraPosition?.target
        }

    override fun onMapReady(map: NaverMap) {
        super.onMapReady(map)
        this.map = map

        viewModel.storeLocation.observe(this) {
            it?.let {
                moveCamera(LatLng(it.latitude, it.longitude))
                addMarker(R.drawable.ic_marker, LatLng(it.latitude, it.longitude))
            }
        }
        map.uiSettings.setLogoMargin(SizeUtils.dpToPx(30f), 0, 0, SizeUtils.dpToPx(50f))
    }

    override fun onMyLocationLoaded(position: LatLng) {
        updateLocationText(position)
    }

    private fun updateLocationText(position: LatLng) {
        binding.tvLocation.text = getCurrentLocationName(position)
        binding.tvLocation.visibility = if (binding.tvLocation.text.isNullOrBlank()) View.GONE else View.VISIBLE
    }
}