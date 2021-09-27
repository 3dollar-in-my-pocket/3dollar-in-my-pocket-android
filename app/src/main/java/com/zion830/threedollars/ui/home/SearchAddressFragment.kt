package com.zion830.threedollars.ui.home

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
                viewModel.requestStoreInfo(LatLng(item.x.toDouble(), item.y.toDouble()))
                activity?.supportFragmentManager?.popBackStack()
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
            adapter.submitList(it.addresses)
        }
    }
}