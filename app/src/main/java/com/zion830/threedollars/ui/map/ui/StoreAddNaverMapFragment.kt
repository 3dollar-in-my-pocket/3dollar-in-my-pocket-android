package com.zion830.threedollars.ui.map.ui

import android.annotation.SuppressLint
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import com.naver.maps.geometry.LatLng
import com.naver.maps.map.NaverMap
import com.zion830.threedollars.ui.edit.viewModel.EditStoreContract
import com.zion830.threedollars.ui.edit.viewModel.EditStoreViewModel
import com.zion830.threedollars.utils.SizeUtils
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class StoreAddNaverMapFragment : NaverMapFragment() {

    private val editStoreViewModel: EditStoreViewModel by activityViewModels()

    private var isIdleAvailable = false

    private var lastTime: Long = 0

    @SuppressLint("ClickableViewAccessibility")
    override fun onMapReady(map: NaverMap) {
        super.onMapReady(map)

        naverMap?.addOnCameraIdleListener {
            if (System.currentTimeMillis() - lastTime > 1000 && isIdleAvailable) {
                val selectedPosition = naverMap?.cameraPosition?.target
                editStoreViewModel.processIntent(EditStoreContract.Intent.UpdateTempLocation(selectedPosition))
                lastTime = System.currentTimeMillis()
            }
        }

        val params = binding.btnFindLocation.layoutParams as ViewGroup.MarginLayoutParams
        params.setMargins(0, 0, SizeUtils.dpToPx(24f), SizeUtils.dpToPx(58f))
        binding.btnFindLocation.layoutParams = params

        editStoreViewModel.state.value.selectedLocation?.let { location ->
            editStoreViewModel.processIntent(EditStoreContract.Intent.UpdateTempLocation(location))
            moveCamera(location)
            isIdleAvailable = true
        }
    }

    override fun onMyLocationLoaded(position: LatLng) {
        super.onMyLocationLoaded(position)
    }
}