package com.zion830.threedollars.ui.store_detail.map

import android.content.Context
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

class StoreDetailNaverMapFragment : NaverMapFragment() {
    val viewModel: StoreDetailViewModel by activityViewModels()

    private var listener: OnMapTouchListener? = null

    override fun onAttach(context: Context) {
        super.onAttach(context)
        listener = requireActivity() as? OnMapTouchListener
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

        viewModel.storeLocation.observe(this) {
            it?.let {
                moveCamera(LatLng(it.latitude, it.longitude))
                addMarker(R.drawable.ic_store_selected, LatLng(it.latitude, it.longitude))
            }
        }
        map.uiSettings.setLogoMargin(SizeUtils.dpToPx(30f), 0, 0, SizeUtils.dpToPx(50f)) // 로고 가려지도록
    }
}