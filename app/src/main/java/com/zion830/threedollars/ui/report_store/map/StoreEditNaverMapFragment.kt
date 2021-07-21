package com.zion830.threedollars.ui.report_store.map

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

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = super.onCreateView(inflater, container, savedInstanceState)

        val frameLayout = TouchableWrapper(requireActivity(), null, 0, listener)
        frameLayout.setBackgroundColor(ContextCompat.getColor(requireContext(), android.R.color.transparent))
        (binding.root as? ViewGroup)?.addView(
            frameLayout,
            ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
        )

        return view
    }

    override fun onMapReady(map: NaverMap) {
        super.onMapReady(map)
        this.map = map

        naverMap?.addOnCameraIdleListener {
            val selectedPosition = naverMap?.cameraPosition?.target
            onMapUpdated(selectedPosition)
        }

        viewModel.storeLocation.observe(this) {
            it?.let {
                moveCamera(LatLng(it.latitude, it.longitude))
                addMarker(R.drawable.ic_store_selected, LatLng(it.latitude, it.longitude))
            }
        }
    }
}