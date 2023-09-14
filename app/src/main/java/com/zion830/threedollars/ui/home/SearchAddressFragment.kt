package com.zion830.threedollars.ui.home

import android.os.Bundle
import android.view.KeyEvent
import android.view.inputmethod.EditorInfo
import androidx.core.view.isVisible
import androidx.fragment.app.activityViewModels
import com.naver.maps.geometry.LatLng
import com.threedollar.network.data.kakao.Document
import com.zion830.threedollars.Constants
import com.zion830.threedollars.EventTracker
import com.zion830.threedollars.R
import com.zion830.threedollars.databinding.FragmentSearchByAddressBinding
import com.zion830.threedollars.ui.home.adapter.SearchAddressRecyclerAdapter
import com.zion830.threedollars.utils.NaverMapUtils
import com.zion830.threedollars.utils.showToast
import dagger.hilt.android.AndroidEntryPoint
import zion830.com.common.base.LegacyBaseFragment
import zion830.com.common.listener.OnItemClickListener

@AndroidEntryPoint
class SearchAddressFragment : LegacyBaseFragment<FragmentSearchByAddressBinding, HomeViewModel>(R.layout.fragment_search_by_address) {

    override val viewModel: HomeViewModel by activityViewModels()

    private val searchViewModel: SearchAddressViewModel by activityViewModels()

    private lateinit var adapter: SearchAddressRecyclerAdapter

    override fun initView() {
        binding.etSearch.requestFocus()

        adapter = SearchAddressRecyclerAdapter(object : OnItemClickListener<Document> {
            override fun onClick(item: Document) {
                val location = LatLng(item.y.toDouble(), item.x.toDouble())
                if (arguments?.getBoolean(IS_ROAD_FOOD_MODE) == true) {
                    viewModel.requestHomeItem(location)
                } else {
                    viewModel.getBossNearStore(location)
                }
                searchViewModel.updateLatLng(location)
                activity?.supportFragmentManager?.popBackStack()
                searchViewModel.clear()
            }
        })
        binding.rvSearchResult.adapter = adapter
        binding.btnBack.setOnClickListener {
            EventTracker.logEvent(Constants.CLOSE_BTN_CLICKED)
            requireActivity().onBackPressed()
        }
        binding.btnSearch.setOnClickListener {
            EventTracker.logEvent(Constants.LOCATION_ITEM_CLICKED)
            if (binding.etSearch.text.isNullOrBlank()) {
                showToast("검색어가 없습니다.")
            } else {
                searchViewModel.search(binding.etSearch.text.toString(), viewModel.currentLocation.value ?: NaverMapUtils.DEFAULT_LOCATION)
            }
        }
        searchViewModel.searchResult.observe(viewLifecycleOwner) {
            adapter.submitList(it?.documents ?: emptyList())
            binding.tvEmpty.isVisible = it?.documents.isNullOrEmpty()
        }

        binding.etSearch.setOnEditorActionListener { _, actionId, keyEvent ->
            if (actionId == EditorInfo.IME_ACTION_DONE ||
                (keyEvent != null && keyEvent.keyCode == KeyEvent.KEYCODE_ENTER)
            ) {
                binding.btnSearch.performClick()
            }
            false
        }
    }

    companion object {

        const val IS_ROAD_FOOD_MODE = "IS_ROAD_FOOD_MODE"

        fun newInstance(isRoadFoodMode: Boolean) =
            SearchAddressFragment().apply {
                arguments = Bundle().apply {
                    putBoolean(IS_ROAD_FOOD_MODE, isRoadFoodMode)
                }
            }
    }
}