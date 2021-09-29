package com.zion830.threedollars.ui.home

import android.view.KeyEvent
import android.view.inputmethod.EditorInfo
import androidx.core.view.isVisible
import androidx.fragment.app.activityViewModels
import com.naver.maps.geometry.LatLng
import com.zion830.threedollars.R
import com.zion830.threedollars.databinding.FragmentSearchByAddressBinding
import com.zion830.threedollars.repository.model.response.Addresse
import com.zion830.threedollars.ui.home.adapter.SearchAddressRecyclerAdapter
import com.zion830.threedollars.utils.showToast
import zion830.com.common.base.BaseFragment
import zion830.com.common.listener.OnItemClickListener

class SearchAddressFragment : BaseFragment<FragmentSearchByAddressBinding, HomeViewModel>(R.layout.fragment_search_by_address) {

    override val viewModel: HomeViewModel by activityViewModels()

    private val searchViewModel: SearchAddressViewModel by activityViewModels()

    private lateinit var adapter: SearchAddressRecyclerAdapter

    override fun initView() {
        adapter = SearchAddressRecyclerAdapter(object : OnItemClickListener<Addresse> {
            override fun onClick(item: Addresse) {
                val location = LatLng(item.y.toDouble(), item.x.toDouble())
                viewModel.requestStoreInfo(location)
                searchViewModel.updateLatLng(location)
                activity?.supportFragmentManager?.popBackStack()
                searchViewModel.clear()
            }
        })
        binding.rvSearchResult.adapter = adapter

        binding.btnSearch.setOnClickListener {
            if (binding.etSearch.text.isNullOrBlank()) {
                showToast("검색어가 없습니다.")
            } else {
                searchViewModel.search(binding.etSearch.text.toString())
            }
        }
        searchViewModel.searchResult.observe(viewLifecycleOwner) {
            adapter.submitList(it?.addresses ?: emptyList())
            binding.tvEmpty.isVisible = it?.addresses.isNullOrEmpty()
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
}