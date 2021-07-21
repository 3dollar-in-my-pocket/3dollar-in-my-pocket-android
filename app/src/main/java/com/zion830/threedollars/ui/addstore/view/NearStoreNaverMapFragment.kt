package com.zion830.threedollars.ui.addstore.view

import android.content.Intent
import android.view.ViewGroup.MarginLayoutParams
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.observe
import com.naver.maps.geometry.LatLng
import com.naver.maps.map.NaverMap
import com.zion830.threedollars.Constants
import com.zion830.threedollars.R
import com.zion830.threedollars.customview.NaverMapFragment
import com.zion830.threedollars.ui.home.HomeViewModel
import com.zion830.threedollars.utils.SizeUtils


class NearStoreNaverMapFragment : NaverMapFragment() {
    val viewModel: HomeViewModel by activityViewModels()

    override fun onMapReady(map: NaverMap) {
        super.onMapReady(map)

        val params = binding.btnFindLocation.layoutParams as MarginLayoutParams
        params.setMargins(0, 0, SizeUtils.dpToPx(24f), SizeUtils.dpToPx(120f))
        binding.btnFindLocation.layoutParams = params

        viewModel.nearStoreInfo.observe(viewLifecycleOwner) { res ->
            addMarkers(R.drawable.ic_store_selected, res.map { LatLng(it.latitude, it.longitude) })
        }
        moveToCurrentLocation()
    }

    override fun onMyLocationLoaded(position: LatLng) {
        viewModel.requestStoreInfo(position)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == Constants.GET_LOCATION_PERMISSION) {
            moveToCurrentLocation()
        }
    }
}