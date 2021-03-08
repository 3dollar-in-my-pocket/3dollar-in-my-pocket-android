package com.zion830.threedollars.ui.store_detail.map

import android.content.Intent
import android.util.Log
import android.view.View
import android.view.ViewGroup.MarginLayoutParams
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.observe
import com.naver.maps.geometry.LatLng
import com.naver.maps.map.NaverMap
import com.zion830.threedollars.Constants
import com.zion830.threedollars.R
import com.zion830.threedollars.customview.NaverMapFragment
import com.zion830.threedollars.repository.model.MenuType
import com.zion830.threedollars.ui.store_detail.vm.StoreByMenuViewModel
import com.zion830.threedollars.utils.NaverMapUtils
import com.zion830.threedollars.utils.SizeUtils
import com.zion830.threedollars.utils.getCurrentLocationName


class StoreByMenuNaverMapFragment : NaverMapFragment() {
    val viewModel: StoreByMenuViewModel by activityViewModels()

    override fun onMapReady(map: NaverMap) {
        super.onMapReady(map)

        val params = binding.btnFindLocation.layoutParams as MarginLayoutParams
        params.setMargins(0, 0, SizeUtils.dpToPx(24f), SizeUtils.dpToPx(24f))
        binding.btnFindLocation.layoutParams = params

        viewModel.storeByRating.observe(this) { res ->
            Log.d("??", res.getAllStores().size.toString())
            addMarkers(R.drawable.ic_store_selected, res.getAllStores().map { LatLng(it.latitude, it.longitude) })
        }
        viewModel.storeByDistance.observe(this) { res ->
            Log.d("??", res.getAllStores().size.toString())
            addMarkers(R.drawable.ic_store_selected, res.getAllStores().map { LatLng(it.latitude, it.longitude) })
        }

        moveToCurrentLocation()
    }

    override fun onMyLocationLoaded(position: LatLng) {
        super.onMyLocationLoaded(position)
        updateLocationText(position)
        viewModel.changeCategory(viewModel.category.value ?: MenuType.BUNGEOPPANG, position)
        viewModel.requestStoreInfo(position)
    }

    private fun updateLocationText(position: LatLng) {
        binding.tvLocation.text = getCurrentLocationName(position)
        binding.tvLocation.visibility = if (binding.tvLocation.text.isNullOrBlank()) View.GONE else View.VISIBLE
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            Constants.SHOW_STORE_BY_CATEGORY -> {
                viewModel.requestStoreInfo(currentPosition ?: NaverMapUtils.DEFAULT_LOCATION)
            }
            Constants.ADD_STORE -> {
                viewModel.requestStoreInfo(currentPosition ?: NaverMapUtils.DEFAULT_LOCATION)
            }
            Constants.GET_LOCATION_PERMISSION -> {
                moveToCurrentLocation()
                viewModel.requestStoreInfo(currentPosition ?: NaverMapUtils.DEFAULT_LOCATION)
            }
        }
    }
}